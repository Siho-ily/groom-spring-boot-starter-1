package com.study.profile_stack_api.domain.auth.dto.response;

import lombok.Getter;

@Getter
public class LogoutResponse {
    private final String message = "로그아웃 되었습니다.";
}
