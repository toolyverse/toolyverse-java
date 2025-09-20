package toolyverse.io.toolyverse.domain.otp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import toolyverse.io.toolyverse.domain.otp.enumeration.OtpSendingChannel;
import toolyverse.io.toolyverse.domain.otp.enumeration.OtpType;
import toolyverse.io.toolyverse.domain.otp.model.request.OtpRequest;
import toolyverse.io.toolyverse.domain.otp.model.request.ValidationRequest;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class OtpService {

    private final StringRedisTemplate stringRedisTemplate;
    private final Map<OtpSendingChannel, OtpSender> senders = new EnumMap<>(OtpSendingChannel.class);

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 8;
    private static final String OTP_KEY_PREFIX = "otp:";

    /**
     * Spring injects all beans that implement OtpSender and StringRedisTemplate
     * StringRedisTemplate is automatically provided by Spring Boot
     */
    public OtpService(StringRedisTemplate stringRedisTemplate, List<OtpSender> senderList) {
        this.stringRedisTemplate = stringRedisTemplate;
        for (OtpSender sender : senderList) {
            senders.put(sender.getChannel(), sender);
        }
    }

    /**
     * Generates, saves, and sends an OTP using Redis with automatic TTL
     */
    public void generateAndSendOtp(OtpRequest request) {
        // Validation
        if (request == null || request.destination() == null) {
            throw new IllegalArgumentException("Invalid OTP request.");
        }

        String redisKey = buildRedisKey(request.destination(), request.type(), request.sendingChannel());

        // Check if OTP already exists (not expired)
        String existingOtp = stringRedisTemplate.opsForValue().get(redisKey);
        if (existingOtp != null) {
            log.info("OTP already sent and still valid for {}.", request.destination());
            return; // Exit the method - OTP still valid
        }

        // Generate new OTP
        String otp = generateNumericOtp();

        // Save to Redis with automatic TTL
        stringRedisTemplate.opsForValue().set(redisKey, otp, OTP_EXPIRY_MINUTES, TimeUnit.MINUTES);

        // Find the correct sender and send the notification
        OtpSender sender = senders.get(request.sendingChannel());
        if (sender == null) {
            log.error("No OTP sender found for channel: {}", request.sendingChannel());
            throw new UnsupportedOperationException("Unsupported OTP channel: " + request.sendingChannel());
        }

        sender.send(request.destination(), otp);
        log.info("OTP sent successfully to {} via {} (expires in {} minutes)",
                request.destination(), request.sendingChannel(), OTP_EXPIRY_MINUTES);
    }

    /**
     * Validates a user-provided OTP using Redis
     */
    public boolean validateOtp(ValidationRequest request) {
        String redisKey = buildRedisKey(request.destination(), request.type(), request.sendingChannel());

        // Get OTP from Redis
        String storedOtp = stringRedisTemplate.opsForValue().get(redisKey);

        if (storedOtp == null || !storedOtp.equals(request.otp())) {
            log.warn("Invalid or expired OTP for destination: {}", request.destination());
            return false;
        }

        // OTP is valid, delete it so it can't be reused
        stringRedisTemplate.delete(redisKey);
        log.info("OTP validated successfully for {}", request.destination());
        return true;
    }

    /**
     * Builds a unique Redis key for the OTP
     * Format: otp:destination:type:channel
     */
    private String buildRedisKey(String destination, OtpType type,
                                 OtpSendingChannel channel) {
        return OTP_KEY_PREFIX + destination + ":" + type + ":" + channel;
    }

    /**
     * Generates a numeric OTP
     */
    private String generateNumericOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
}