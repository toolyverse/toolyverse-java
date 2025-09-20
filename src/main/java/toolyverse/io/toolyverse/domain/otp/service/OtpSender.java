package toolyverse.io.toolyverse.domain.otp.service;


import toolyverse.io.toolyverse.domain.otp.enumeration.OtpSendingChannel;

public interface OtpSender {
    void send(String destination, String otp);

    OtpSendingChannel getChannel();
}
