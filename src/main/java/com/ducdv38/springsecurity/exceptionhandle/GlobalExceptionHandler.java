package com.ducdv38.springsecurity.exceptionhandle;

import com.ducdv38.springsecurity.dto.response.ApiResponse;
import com.ducdv38.springsecurity.utils.Constants;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> exceptionHandler(Exception e) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage(e.getMessage());
        apiResponse.setCode(ErrorCode.UNAUTHENTICATED.getCode());
        return  ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> appExceptiontionHandler(AppException e) {
        ErrorCode errorCode = e.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage(e.getMessage());
        apiResponse.setCode(errorCode.getCode());
        return  ResponseEntity
                .status(errorCode.getHttpStatusCode())
                .body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> accessDeniedExceptionHandler(AccessDeniedException e) {
        ErrorCode errorCode = ErrorCode.FORBIDDEN;
        return  ResponseEntity.status(errorCode.getHttpStatusCode()).body(
                ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<?>> handlingValidation(MethodArgumentNotValidException e) {
        String enumKey = e.getFieldError().getDefaultMessage();
        ErrorCode errorCode = ErrorCode.INVALID_MESSAGE_KEY;
        Map<String, Object> attribute = null;
        try {
            errorCode = ErrorCode.valueOf(enumKey);
            // get attribute from annotation
            var constraintViolations = e.getBindingResult().getAllErrors().getFirst().unwrap(ConstraintViolation.class);
            attribute = constraintViolations.getConstraintDescriptor().getAttributes();

        } catch (IllegalArgumentException iae) {
            log.error(iae.getMessage());
        }
        ApiResponse<?> apiResponse = new ApiResponse();
        apiResponse.setMessage(Objects.nonNull(attribute) ? mapAttributes(errorCode.getMessage(),attribute)
                : errorCode.getMessage());
        apiResponse.setCode(errorCode.getCode());
        return  ResponseEntity.badRequest().body(apiResponse);
    }

// extract attribute and replace value of attribute when customize validation
private String mapAttributes(String message, Map<String, Object> attributes) {
    for (Map.Entry<String, Object> entry : attributes.entrySet()) {
        String placeholder = "{" + entry.getKey() + "}";
        message = message.replace(placeholder, entry.getValue().toString());
    }
    return message;
}

}
