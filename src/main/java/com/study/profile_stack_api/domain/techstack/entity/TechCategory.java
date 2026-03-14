package com.study.profile_stack_api.domain.techstack.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum TechCategory {
    // 인스턴스
    LANGUAGE("프로그래밍 언어", "📝"),
    FRAMEWORK("프레임워크", "🏗️"),
    DATABASE("데이터베이스", "💾"),
    DEVOPS("DevOps/인프라", "☁️"),
    TOOL("개발 도구", "🔧"),
    ETC("기타", "📦");

    // 필드
    private final String description;
    private final String icon;

    // 검증
    public static boolean exists(String name) {
        return Arrays.stream(values())
                .anyMatch(e -> e.name().equals(name));
    }
}
