package com.study.profile_stack_api.global.exception.validation.request;

import com.study.profile_stack_api.global.exception.BusinessException;
import com.study.profile_stack_api.global.exception.common.ErrorCode;

public class InvalidRequestValueException extends BusinessException {
    public InvalidRequestValueException(String key, String value) {
        super(ErrorCode.INVALID_INPUT, "유효하지 않은 요청 필드 입니다. (%s, %s)".formatted(key, value));
    }

    public InvalidRequestValueException(String reason, String key, String value) {
        super(ErrorCode.INVALID_INPUT, "%s (%s, %s)".formatted(reason, key, value));
    }
}
