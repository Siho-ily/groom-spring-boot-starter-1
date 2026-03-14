package com.study.profile_stack_api.global.exception;

import com.study.profile_stack_api.global.error.ErrorCode;

public class InvalidRequestField extends BusinessException {
    public InvalidRequestField(String key, String value) {
        super(ErrorCode.INVALID_INPUT, "유효하지 않은 요청 필드 입니다. (%s, %s)".formatted(key, value));
    }
}
