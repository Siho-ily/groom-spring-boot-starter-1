package com.study.profile_stack_api.domain.techstack.dto.response;

import com.study.profile_stack_api.domain.techstack.entity.Proficiency;
import com.study.profile_stack_api.domain.techstack.entity.TechCategory;
import com.study.profile_stack_api.domain.techstack.entity.TechStack;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TechStackResponse {
    /**
     *  {
     *  "id": 1,
     *  "profileId": 1,
     *  "name": "Java",
     *  "category": "LANGUAGE",
     *  "categoryIcon": "📝",
     *  "proficiency": "ADVANCED",
     *  "proficiencyIcon": "🌳",
     *  "yearsOfExp": 3,
     *  "createdAt": "2025-01-20T10:00:00",
     *  "updatedAt": "2025-01-20T10:00:00"
     *  }
     */
    private Long id;                    // 기술 스택 고유 ID
    private Long profileId;             // 프로필 ID (FK)
    private String name;                // 기술명
    private TechCategory category;      // 기술 카테고리
    private Proficiency proficiency;    // 숙련도
    private Integer yearsOfExp;         // 사용 경험(년)
    private LocalDateTime createdAt;    // 생성 일시
    private LocalDateTime updatedAt;    // 수정 일시

    public static TechStackResponse from(TechStack techStack) {
        return new TechStackResponse(
                techStack.getId(),
                techStack.getProfileId(),
                techStack.getName(),
                techStack.getCategory(),
                techStack.getProficiency(),
                techStack.getYearsOfExp(),
                techStack.getCreatedAt(),
                techStack.getUpdatedAt()
        );
    }
}
