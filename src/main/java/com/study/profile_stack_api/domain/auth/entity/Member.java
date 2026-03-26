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
public class Member {
    private Long id;                    // id
    private String userId;              // 유저 id
    private String password;            // Bcrypt로 인코딩된 패스워드
    private String role;                // 권한
    private LocalDateTime createAt;     // 생성일
}
