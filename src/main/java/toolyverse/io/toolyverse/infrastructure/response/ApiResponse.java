package toolyverse.io.toolyverse.infrastructure.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import toolyverse.io.toolyverse.infrastructure.config.message.MessageUtil;

import java.util.Map;

@JsonPropertyOrder({
        "error",
        "traceId",
        "businessErrorCode",
        "message",
        "data",
        "validationErrors",
})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class ApiResponse<T> {

    // Define the default message keys
    private static final String DEFAULT_SUCCESS_KEY = "messages.default.success";
    private static final String DEFAULT_ERROR_KEY = "messages.default.error";

    // Common fields
    private Boolean error;
    private String message;
    @Builder.Default
    private String traceId = MDC.get("traceId");

    // Success response field
    private T data;

    // Error response fields
    private Integer businessErrorCode;
    private Map<String, String> validationErrors;

    public static <T> ApiResponse<T> success(T data) {
        return success(data, DEFAULT_SUCCESS_KEY);
    }

    public static <T> ApiResponse<T> success(T data, String messageKey) {
        return ApiResponse.<T>builder()
                .error(false)
                .message(MessageUtil.getMessage(messageKey))
                .data(data)
                .build();
    }

    public static <U> ApiResponse<PageableResponse<U>> success(Page<U> page) {
        return success(page, DEFAULT_SUCCESS_KEY);
    }

    public static <U> ApiResponse<PageableResponse<U>> success(Page<U> page, String messageKey) {
        PageableResponse.PageDetails pageDetails = PageableResponse.PageDetails.builder()
                .totalElements(page.getTotalElements())
                .numberOfElements(page.getNumberOfElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .first(page.isFirst())
                .empty(page.isEmpty())
                .build();

        PageableResponse<U> pageableResponse = PageableResponse.<U>builder()
                .content(page.getContent())
                .pageable(pageDetails)
                .build();

        return ApiResponse.<PageableResponse<U>>builder()
                .error(false)
                .message(MessageUtil.getMessage(messageKey))
                .data(pageableResponse)
                .build();
    }

    public static ApiResponse<Object> successWithEmptyData() {
        return successWithEmptyData(DEFAULT_SUCCESS_KEY);
    }

    public static ApiResponse<Object> successWithEmptyData(String messageKey) {
        return ApiResponse.builder()
                .error(false)
                .message(MessageUtil.getMessage(messageKey))
                .data(null)
                .build();
    }

    public static ApiResponse<Object> error(Integer businessErrorCode, Map<String, String> validationErrors) {
        return error(DEFAULT_ERROR_KEY, businessErrorCode, validationErrors);
    }

    public static ApiResponse<Object> error(String messageKey, Integer businessErrorCode,
                                            Map<String, String> validationErrors) {
        return ApiResponse.builder()
                .error(true)
                .message(MessageUtil.getMessage(messageKey))
                .businessErrorCode(businessErrorCode)
                .validationErrors(validationErrors)
                .build();
    }

    public static ApiResponse<Object> error(Integer businessErrorCode, String customMessage,
                                            Map<String, String> validationErrors) {
        return ApiResponse.builder()
                .error(true)
                .message(customMessage)
                .businessErrorCode(businessErrorCode)
                .validationErrors(validationErrors)
                .build();
    }


}

