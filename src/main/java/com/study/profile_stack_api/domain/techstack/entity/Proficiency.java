package com.study.profile_stack_api.domain.techstack.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Proficiency {
    // 인스턴스
    BEGINNER("입문", "🌱"),
    INTERMEDIATE("중급", "🌿"),
    ADVANCED("고급", "🌳"),
    EXPERT("전문가", "🏆");

    // 필드
    private final String description;
    private final String icon;
}
