package com.study.profile_stack_api.global.exception.domain.auth;

import com.study.profile_stack_api.global.exception.BusinessException;
import com.study.profile_stack_api.global.exception.common.ErrorCode;

public class DuplicateMemberUsernameException extends BusinessException {
    public DuplicateMemberUsernameException(String username) {
        super(ErrorCode.DUPLICATE_MEMBER_USERNAME, "이미 사용중인 유저 네임 입니다.: " + username);
    }
}
