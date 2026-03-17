package com.study.profile_stack_api.domain.profile.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileCreateRequest {
    @NotNull(message = "멤버 ID는 필수 입니다.")
    private Long memberId;

    @NotBlank(message = "이름는 필수 입니다.")
    @Size(max = 50, message = "이름은 50자 이하여야 합니다.")
    private String name;

    @Email(message = "올바른 이메일 형식이 아닙니다. (ex. email@example.com)")
    @NotBlank(message = "이메일은 필수 입니다.")
    @Size(max = 100, message = "이메일은 100자 이하여야 합니다.")
    private String email;

    @Size(max = 500, message = "자기소개는 500자 이하여야 합니다.")
    private String bio;

    @NotBlank(message = "직무는 필수 입니다.")
    @Size(max = 20, message = "직무는 20자 이하여야 합니다.")
    private String position;

    @NotNull(message = "경력은 필수 입니다.")
    @Min(value = 1, message = "경력은 1년 이상어야 합니다.")
    private Integer careerYears;

    @Size(max = 200, message = "깃허브 URL은 200자 이하여야 합니다.")
    private String githubUrl;

    @Size(max = 200, message = "블로그 URL은 200자 이하여야 합니다.")
    private String blogUrl;
}
