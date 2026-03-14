package com.study.profile_stack_api.global.exception.validation.request;

import com.study.profile_stack_api.global.exception.BusinessException;
import com.study.profile_stack_api.global.exception.common.ErrorCode;

public class DuplicateEmailException extends BusinessException {
    public DuplicateEmailException() {
        super(
                ErrorCode.DUPLICATE_EMAIL,
                "이미 사용 중인 이메일입니다."
        );
    }
}