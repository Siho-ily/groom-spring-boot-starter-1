package com.study.profile_stack_api.domain.profile.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileDeleteResponse {
    private Long id;
    private String message;

    public static ProfileDeleteResponse from(Long id) {
        return new ProfileDeleteResponse(id, "프로필이 성공적으로 삭제되었습니다.");
    }
}
