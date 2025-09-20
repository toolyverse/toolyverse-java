package toolyverse.io.toolyverse.infrastructure.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionMessage {
    DEFAULT_EXCEPTION("messages.error.default_message", HttpStatus.BAD_REQUEST, 1000),
    ALREADY_EXISTS_EXCEPTION("messages.error.already_exists_exception", HttpStatus.CONFLICT, 1001),
    NOT_FOUND_EXCEPTION("messages.error.not_found_exception", HttpStatus.NOT_FOUND, 1002),
    PRODUCT_NOT_FOUND_EXCEPTION("messages.error.product_not_found_exception", HttpStatus.NOT_FOUND, 1002),
    ;

    private final String key;
    private final HttpStatus status;
    private final int businessErrorCode;

    ExceptionMessage(String key, HttpStatus httpStatus, int businessErrorCode) {
        this.key = key;
        this.status = httpStatus;
        this.businessErrorCode = businessErrorCode;
    }
}