package toolyverse.io.toolyverse.domain.otp.enumeration;

import lombok.Getter;

@Getter
public enum OtpType {
    REGISTER_USER_VERIFICATION, CHANGE_PASSWORD, RESET_PASSWORD, DEVICE_CHANGE;
}
