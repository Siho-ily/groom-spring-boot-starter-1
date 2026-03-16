package com.study.profile_stack_api.domain.profile.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileUpdateRequest{
    private String name;
    private String email;
    private String bio;
    private String position;
    private Integer careerYears;
    private String githubUrl;
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
