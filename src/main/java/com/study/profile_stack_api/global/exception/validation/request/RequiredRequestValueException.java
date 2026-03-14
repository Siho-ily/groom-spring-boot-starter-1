package com.study.profile_stack_api.global.exception.validation.request;

import com.study.profile_stack_api.global.exception.BusinessException;
import com.study.profile_stack_api.global.exception.common.ErrorCode;

public class RequiredRequestValueException extends BusinessException {
    public RequiredRequestValueException(String key) {
        super(ErrorCode.INVALID_INPUT, "필수 입력 필드가 누락되었습니다. (%s)".formatted(key));
    }
}
