package com.study.profile_stack_api.global.exception.domain.auth;

import com.study.profile_stack_api.global.exception.BusinessException;
import com.study.profile_stack_api.global.exception.common.ErrorCode;

public class RefreshTokenNotFoundException extends BusinessException {
    public RefreshTokenNotFoundException() {
        super(ErrorCode.REFRESH_TOKEN_NOT_FOUND,"리프레시 토큰을 찾을 수 없습니다.");
    }
}
