package com.study.profile_stack_api.global.exception.domain.auth;

import com.study.profile_stack_api.global.exception.BusinessException;
import com.study.profile_stack_api.global.exception.common.ErrorCode;

public class InvalidTokenException extends BusinessException {
    public InvalidTokenException(String message) {
        super(ErrorCode.INVALID_TOKEN, "유효하지 않은 토큰입니다. : " + message);
    }
}
