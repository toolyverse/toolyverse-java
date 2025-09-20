package toolyverse.io.toolyverse.domain.otp.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import toolyverse.io.toolyverse.domain.otp.enumeration.OtpSendingChannel;
import toolyverse.io.toolyverse.domain.otp.service.OtpSender;
import toolyverse.io.toolyverse.infrastructure.config.email.EmailSenderService;
import toolyverse.io.toolyverse.infrastructure.config.email.HtmlEmailRequest;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailOtpSender implements OtpSender {


    private final EmailSenderService emailSenderService;

    @Override
    public void send(String destination, String otp) {
        // html reset password template
        var htmlEmailRequest = new HtmlEmailRequest("OTP Verify", destination,
                "email/otp_verify_email");
        Map<String, Object> model = new HashMap<>();
        model.put("otp_code", otp);
        emailSenderService.sendTemplatedHtmlEmail(htmlEmailRequest, model);
        log.info("Sending EMAIL OTP {} to: {}", otp, destination);
    }

    @Override
    public OtpSendingChannel getChannel() {
        return OtpSendingChannel.EMAIL;
    }
}
