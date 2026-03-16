package com.study.profile_stack_api.domain.techstack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TechStackDeleteResponse {
    private Long id;
    private String message;

    public static TechStackDeleteResponse from(Long id) {
        return new TechStackDeleteResponse(id, "프로필이 성공적으로 삭제되었습니다.");
    }
}
