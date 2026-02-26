package com.study.profile_stack_api.global.exception;

import com.study.profile_stack_api.global.error.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode,  String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
