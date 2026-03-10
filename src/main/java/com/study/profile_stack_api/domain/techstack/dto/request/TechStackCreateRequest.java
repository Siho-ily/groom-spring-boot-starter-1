package com.study.profile_stack_api.domain.techstack.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TechStackCreateRequest {
    private String name;            // 기술명 (1 ~ 50자)
    private String category;        // 기술 카테고리
    private String proficiency;     // 숙련도
    private Integer yearsOfExp;      // 사용 경험(년, 0이상);
}
