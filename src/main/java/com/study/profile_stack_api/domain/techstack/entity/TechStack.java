package com.study.profile_stack_api.domain.techstack.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tech_stack")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechStack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TechCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Proficiency proficiency;

    @Column(name = "years_of_exp", nullable = false)
    private Integer yearsOfExp;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void update(String name, TechCategory category, Proficiency proficiency, Integer yearsOfExp) {
        // null이 아닌 필드만 수정
        if (name != null) {this.name = name;}
        if (category != null) {this.category = category;}
        if (proficiency != null) {this.proficiency = proficiency;}
        if (yearsOfExp != null) {this.yearsOfExp = yearsOfExp;}

        // 수정 시간 업데이트
        this.updatedAt = LocalDateTime.now();
    }
}
