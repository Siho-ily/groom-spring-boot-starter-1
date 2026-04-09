package com.study.profile_stack_api.global.exception.domain.auth;

import com.study.profile_stack_api.global.exception.BusinessException;
import com.study.profile_stack_api.global.exception.common.ErrorCode;

public class MemberNotFoundException extends BusinessException {
    public MemberNotFoundException(Long id) {
        super(ErrorCode.MEMBER_NOT_FOUND,"멤버를 찾을 수 없습니다. id : "+id);
    }
}
