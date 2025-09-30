package toolyverse.io.toolyverse.infrastructure.exception;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import toolyverse.io.toolyverse.infrastructure.config.message.MessageUtil;
import toolyverse.io.toolyverse.infrastructure.response.ApiResponseWrapper;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Hidden // for openapi doc
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<ApiResponseWrapper<Object>> handleCustomException(BusinessException exception) {
        log.warn("Business exception occurred: {}", exception.getMessage());
        ApiResponseWrapper<Object> response = ApiResponseWrapper.error(
                exception.getBusinessErrorCode(),
                exception.getMessage(),
                null);
        return new ResponseEntity<>(response, exception.getStatus());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public final ResponseEntity<ApiResponseWrapper<Object>> handleValidationExceptions(Exception ex) {
        Map<String, String> errors = extractValidationErrors(ex);
        log.warn("Validation failed: {}", errors);
        ApiResponseWrapper<Object> response = ApiResponseWrapper.error("validation.failed",
                ExceptionMessage.DEFAULT_EXCEPTION.getBusinessErrorCode(),
                errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NoResourceFoundException.class})
    public ResponseEntity<ApiResponseWrapper<Object>> noResourceFoundException(NoResourceFoundException exception) {
//        log.warn("Resource not found: {}", exception.getMessage());
        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiResponseWrapper<Object> response = ApiResponseWrapper.error(
                status.value(), MessageUtil.getMessage("error.resource.not.found"),
                null);
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler({Exception.class})
    public final ResponseEntity<ApiResponseWrapper<Object>> handleAllException(Exception ex) {
        String message;
        HttpStatus status = HttpStatus.BAD_REQUEST;
        int errorCode = ExceptionMessage.DEFAULT_EXCEPTION.getBusinessErrorCode();

        if (isDatabaseException(ex)) {
            log.error("Database exception occurred: ", ex);
            message = MessageUtil.getMessage("database.error");
        } else {
            message = ex.getMessage();
        }

        ApiResponseWrapper<Object> response = ApiResponseWrapper.error(errorCode, message, null);
        return new ResponseEntity<>(response, status);
    }

    private Map<String, String> extractValidationErrors(Exception ex) {
        Map<String, String> errors = new HashMap<>();
        if (ex instanceof MethodArgumentNotValidException methodEx) {
            methodEx.getBindingResult().getAllErrors().forEach(error -> {
                String fieldName = (error instanceof FieldError) ?
                        ((FieldError) error).getField() : error.getObjectName();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
        } else if (ex instanceof ConstraintViolationException constraintEx) {
            constraintEx.getConstraintViolations().forEach(violation -> {
                String fieldName = violation.getPropertyPath().toString();
                String errorMessage = violation.getMessage();
                errors.put(fieldName, errorMessage);
            });
        }
        return errors;
    }

    private boolean isDatabaseException(Throwable ex) {
        Throwable cause = ex;
        while (cause != null) {
            if (cause instanceof SQLException ||
                    cause instanceof DataAccessException ||
                    cause instanceof PersistenceException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }
}
