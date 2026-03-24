package com.study.profile_stack_api.global.exception.domain.auth;

import com.study.profile_stack_api.global.exception.BusinessException;
import com.study.profile_stack_api.global.exception.common.ErrorCode;

public class ExpiredTokenException extends BusinessException {
    public ExpiredTokenException() {
        super(ErrorCode.EXPIRED_TOKEN, "토큰이 만료되었습니다. 다시 로그인하여 주십시오.");
    }
}
