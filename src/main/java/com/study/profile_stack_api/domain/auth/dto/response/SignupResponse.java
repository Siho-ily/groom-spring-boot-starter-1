package com.study.profile_stack_api.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupResponse {

    private Long id;
    private String username;

    public static SignupResponse of(Long id, String username) {
        return new SignupResponse(id, username);
    }
}
