package com.study.profile_stack_api.domain.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Role {
    // 인스턴스
    USER("USER"),
    ADMIN("ADMIN");

    // 필드
    private final String name;

    // 검증
    public static boolean exits(String name)
    {
        return Arrays.stream(values())
                .anyMatch(role -> role.name.equals(name));
    }
}
