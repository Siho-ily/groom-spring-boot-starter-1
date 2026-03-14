package com.study.profile_stack_api.global.exception.domain.profile;

import com.study.profile_stack_api.global.exception.common.ErrorCode;
import com.study.profile_stack_api.global.exception.BusinessException;

public class ProfileNotFoundException extends BusinessException {
    public ProfileNotFoundException(Long id) {
        super(
                ErrorCode.PROFILE_NOT_FOUND,
                String.format("id가 %d인 프로필을 찾을 수 없습니다.", id)
        );
    }
}
