package com.study.profile_stack_api.domain.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {
    private Long id;                    // id
    private Long memberId;              // memberId
    private String token;               // 토큰
    private LocalDateTime expiredDate;    // 만료일
    private LocalDateTime createdAt;    // 생성일
}
