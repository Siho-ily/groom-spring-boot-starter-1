package com.study.profile_stack_api.global.exception.domain.auth;

import com.study.profile_stack_api.global.exception.BusinessException;
import com.study.profile_stack_api.global.exception.common.ErrorCode;

public class ForbiddenOwnerMismatch extends BusinessException {
    public ForbiddenOwnerMismatch() {
        super(ErrorCode.FORBIDDEN_OWNER_MISMATCH, "다른 계정의 리소스에 접근할 수 없습니다.");
    }
}
