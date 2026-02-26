package com.study.profile_stack_api.global.exception;

import com.study.profile_stack_api.global.error.ErrorCode;

public class InternalServerErrorException extends BusinessException {
    public InternalServerErrorException() {
        super(
                ErrorCode.INTERNAL_SERVER_ERROR,
                "서버에 오류가 발생하였습니다."
            );
    }
}
