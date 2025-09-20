package toolyverse.io.toolyverse.domain.otp.model.request;


import toolyverse.io.toolyverse.domain.otp.enumeration.OtpSendingChannel;
import toolyverse.io.toolyverse.domain.otp.enumeration.OtpType;

public record OtpRequest(String destination, OtpType type, OtpSendingChannel sendingChannel) {}
