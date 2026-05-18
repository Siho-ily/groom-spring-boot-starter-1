package com.study.profile_stack_api.domain.profile.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 500)
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Position position;

    @Column(name = "career_years", nullable = false)
    private Integer careerYears;

    @Column(name = "github_url", length = 200)
    private String githubUrl;

    @Column(name = "blog_url", length = 200)
    private String blogUrl;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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
