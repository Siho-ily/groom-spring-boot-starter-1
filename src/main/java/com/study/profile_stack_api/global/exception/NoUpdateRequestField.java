package com.study.profile_stack_api.global.exception;

import com.study.profile_stack_api.global.error.ErrorCode;

public class NoUpdateRequestField extends BusinessException {
        public NoUpdateRequestField() {
            super(ErrorCode.INVALID_INPUT, "수정할 필드가 없습니다.");
        }
    }