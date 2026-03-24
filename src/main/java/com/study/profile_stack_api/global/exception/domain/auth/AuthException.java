package com.study.profile_stack_api.global.exception.domain.auth;

import com.study.profile_stack_api.global.exception.BusinessException;
import com.study.profile_stack_api.global.exception.common.ErrorCode;

public class AuthException extends BusinessException {
    public AuthException() {
        super(ErrorCode.UNAUTHORIZED, "인증이 필요한 요청입니다.");
    }
}
