package toolyverse.io.toolyverse.infrastructure.exception;

import org.springframework.stereotype.Component;
import toolyverse.io.toolyverse.infrastructure.config.message.MessageUtil;

@Component
public class ExceptionUtil {

    public static BusinessException buildException() {
        return buildException(ExceptionMessage.DEFAULT_EXCEPTION);
    }

    public static BusinessException buildException(ExceptionMessage ex, Object... args) {
        String errorMessage = MessageUtil.getMessage(ex.getKey(), args);
        return new BusinessException(errorMessage, ex.getStatus(), ex.getBusinessErrorCode());
    }

    public static BusinessException buildException(ExceptionMessage ex) {
        String errorMessage = MessageUtil.getMessage(ex.getKey());
        return new BusinessException(errorMessage, ex.getStatus(), ex.getBusinessErrorCode());
    }

}