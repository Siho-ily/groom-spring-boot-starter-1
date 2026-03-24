package com.study.profile_stack_api.global.exception;

import com.study.profile_stack_api.global.exception.common.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode,  String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
