package com.study.profile_stack_api.domain.techstack.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TechStackDeleteResponse {
    private Long id;
    private String message;

    public static TechStackDeleteResponse of(Long id) {
        TechStackDeleteResponse response = new TechStackDeleteResponse();
        response.setId(id);
        response.setMessage("프로필이 성공적으로 삭제되었습니다.");
        return response;
    }
}
