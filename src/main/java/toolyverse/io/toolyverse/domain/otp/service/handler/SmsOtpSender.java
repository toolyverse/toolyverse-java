package toolyverse.io.toolyverse.domain.otp.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import toolyverse.io.toolyverse.domain.otp.enumeration.OtpSendingChannel;
import toolyverse.io.toolyverse.domain.otp.service.OtpSender;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsOtpSender implements OtpSender {

    @Override
    public void send(String destination, String otp) {
        log.info("Sending SMS OTP {} to: {}", otp, destination);
        // ... logic to send the SMS ...
        // netGsmSendSmsUseCase.execute(...);
    }

    @Override
    public OtpSendingChannel getChannel() {
        return OtpSendingChannel.SMS;
    }
}
