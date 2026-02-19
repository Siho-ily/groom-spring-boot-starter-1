package com.study.profile_stack_api.domain.profile.dto.response;

import com.study.profile_stack_api.domain.profile.entity.Profile;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ProfileResponse {
    private Long id;                    // 프로필 고유 ID
    private String name;                // 이름
    private String email;               // 이메일
    private String bio;                 // 자기소개
    private String position;            // 포지션
    private String positionIcon;       // 포지션 아이콘`
    private Integer careerYears;        // 경력 연차
    private String githubUrl;           // GitHub 주소
    private String blogUrl;             // 블로그 주소
    private LocalDateTime createdAt;    // 생성 일시
    private LocalDateTime updatedAt;    // 수정 일시

    public static ProfileResponse from(Profile profile) {
        ProfileResponse profileResponse = new ProfileResponse();

        profileResponse.id = profile.getId();
        profileResponse.name = profile.getName();
        profileResponse.email = profile.getEmail();
        profileResponse.bio = profile.getBio();
        profileResponse.position = profile.getPosition().getDescription();
        profileResponse.positionIcon = profile.getPosition().getIcon();
        profileResponse.careerYears = profile.getCareerYears();
        profileResponse.githubUrl = profile.getGithubUrl();
        profileResponse.blogUrl = profile.getBlogUrl();
        profileResponse.createdAt = profile.getCreatedAt();
        profileResponse.updatedAt = profile.getUpdatedAt();

        return profileResponse;
    }
}
