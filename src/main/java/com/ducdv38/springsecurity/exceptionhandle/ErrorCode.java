package com.ducdv38.springsecurity.exceptionhandle;

import com.ducdv38.springsecurity.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXISTED(1001, "User already existed", HttpStatus.CONFLICT),
    USER_NOT_EXIST(1002, "User not existed", HttpStatus.NOT_FOUND),
    USER_NAME_INVALID(1003, "Username can't be empty", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004, "Password must be at least {"+ Constants.MIN_ATTRIBUTE+"} characters and at max {"+Constants.MAX_ATTRIBUTE + "} characters", HttpStatus.BAD_REQUEST),
    INVALID_MESSAGE_KEY(1005, "Invalid message key", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(1007, "Access denied: no permission", HttpStatus.FORBIDDEN),
    PERMISSION_NOT_EXIST(1008, "User not existed", HttpStatus.NOT_FOUND),
    DOB_INVALID(1009, "Birth day must be greater {"+ Constants.MIN_ATTRIBUTE+"} year", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(1010, "Role not found" , HttpStatus.NOT_FOUND ),;
    private int code;
    private String message;
    private HttpStatusCode httpStatusCode;

}
