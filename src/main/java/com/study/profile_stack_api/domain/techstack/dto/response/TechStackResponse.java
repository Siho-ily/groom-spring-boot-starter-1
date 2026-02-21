package com.study.profile_stack_api.domain.techstack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
    private Long id;
    private Long profileId;
    private String name;
    private String description;
}
