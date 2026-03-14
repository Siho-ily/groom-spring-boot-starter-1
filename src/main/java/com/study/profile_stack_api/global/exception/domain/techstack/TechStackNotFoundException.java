package com.study.profile_stack_api.global.exception.domain.techstack;

import com.study.profile_stack_api.global.exception.common.ErrorCode;
import com.study.profile_stack_api.global.exception.BusinessException;

public class TechStackNotFoundException extends BusinessException {
    public TechStackNotFoundException(Long id) {
        super(
                ErrorCode.TECH_STACK_NOT_FOUND,
                String.format("id가 %d인 프로필의 테크스택을 찾을 수 없습니다.", id)
        );
    }
}
