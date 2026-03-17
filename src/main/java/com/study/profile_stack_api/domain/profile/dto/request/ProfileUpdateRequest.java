package com.study.profile_stack_api.domain.profile.dto.request;

import com.study.profile_stack_api.global.validation.annotation.NotBlankIfPresent;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileUpdateRequest{
    @NotBlankIfPresent(message = "이름은 빈 값을 가질 수 없습니다.")
    @Size(max = 50, message = "이름은 50자 이하여야 합니다.")
    private String name;

    @NotBlankIfPresent(message = "이메일은 빈 값을 가질 수 없습니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다. (ex. email@example.com)")
    @Size(max = 100, message = "이메일은 100자 이하여야 합니다.")
    private String email;

    @NotBlankIfPresent(message = "자기소개는 빈 값을 가질 수 없습니다.")
    @Size(max = 500, message = "자기소개는 500자 이하여야 합니다.")
    private String bio;

    @NotBlankIfPresent(message = "직무는 빈 값을 가질 수 없습니다.")
    @Size(max=20, message = "직무는 20자 이하여야 합니다.")
    private String position;

    @Min(value = 1, message = "경력은 1년 이상, 100년 이하여야 합니다.")
    @Max(value = 100, message = "경력은 1년 이상, 100년 이하여야 합니다.")
    private Integer careerYears;

    @NotBlankIfPresent(message = "깃허브 URL은 빈 값을 가질 수 없습니다.")
    @Size(max=200, message = "깃허브 URL은 200자 이하여야 합니다.")
    private String githubUrl;

    @NotBlankIfPresent(message = "블로그 URL은 빈 값을 가질 수 없습니다.")
    @Size(max=200, message = "블로그 URL은 200자 이하여야 합니다.")
    private String blogUrl;

    // 전부 null인지 체크용
    public boolean hasNoUpdates() {
        return name == null
                && email == null
                && bio == null
                && position == null
                && careerYears == null
                && githubUrl == null
                && blogUrl == null;
    }
}
