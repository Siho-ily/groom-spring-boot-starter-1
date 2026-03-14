package com.study.profile_stack_api.global.exception.validation.request;

import com.study.profile_stack_api.global.exception.BusinessException;
import com.study.profile_stack_api.global.exception.common.ErrorCode;

public class NoUpdateRequestValueException extends BusinessException {
        public NoUpdateRequestValueException() {
            super(ErrorCode.INVALID_INPUT, "수정할 필드가 없습니다.");
        }
    }