package com.study.profile_stack_api.domain.profile.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {
    private Long id;                    // 프로필 고유 ID
    private Long memberId;              // 멤버 ID
    private String name;                // 이름
    private String email;               // 이메일
    private String bio;                 // 자기소개
    private Position position;          // 직무
    private Integer careerYears;        // 경력 연차
    private String githubUrl;           // GitHub 주소
    private String blogUrl;             // 블로그 주소
    private LocalDateTime createdAt;    // 생성 일시
    private LocalDateTime updatedAt;    // 수정 일시

     public void update(String name, String email, String bio, Position position, Integer careerYears, String githubUrl,  String blogUrl) {
         // null이 아닌 필드만 수정
         if (name != null) {this.name = name;}
         if (email != null) {this.email = email;}
         if (bio != null) {this.bio = bio;}
         if (position != null) {this.position = position;}
         if (careerYears != null) {this.careerYears = careerYears;}
         if (githubUrl != null) {this.githubUrl = githubUrl;}
         if (blogUrl != null) {this.blogUrl = blogUrl;}

         // 수정 시간 업데이트
         updatedAt = LocalDateTime.now();
     }
}
