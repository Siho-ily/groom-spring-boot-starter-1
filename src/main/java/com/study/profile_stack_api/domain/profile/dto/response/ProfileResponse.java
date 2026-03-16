package com.study.profile_stack_api.domain.profile.dto.response;

import com.study.profile_stack_api.domain.profile.entity.Profile;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProfileResponse {
    private Long id;                    // 프로필 고유 ID
    private String name;                // 이름
    private String email;               // 이메일
    private String bio;                 // 자기소개
    private String position;            // 포지션
    private String positionIcon;        // 포지션 아이콘`
    private Integer careerYears;        // 경력 연차
    private String githubUrl;           // GitHub 주소
    private String blogUrl;             // 블로그 주소
    private LocalDateTime createdAt;    // 생성 일시
    private LocalDateTime updatedAt;    // 수정 일시

    public static ProfileResponse from(Profile profile) {
        return new ProfileResponse(
                profile.getId(),
                profile.getName(),
                profile.getEmail(),
                profile.getBio(),
                profile.getPosition().getDescription(),
                profile.getPosition().getIcon(),
                profile.getCareerYears(),
                profile.getGithubUrl(),
                profile.getBlogUrl(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
            );
    }
}
