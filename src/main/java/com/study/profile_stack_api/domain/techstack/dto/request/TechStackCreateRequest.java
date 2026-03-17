package com.study.profile_stack_api.domain.techstack.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TechStackCreateRequest {
    @NotBlank(message = "기술명은 필수 입니다.")
    @Size(min = 1, max = 50, message = "기술명은 1자 이상, 50자 이하여야 합니다.")
    private String name;            // 기술명 (1 ~ 50자)

    @NotBlank(message = "기술 카테고리는 필수 입니다.")
    @Size(min = 1, max = 20, message = "기술 카테고리는 1자 이상, 20자 이하여야 합니다.")
    private String category;        // 기술 카테고리

    @NotBlank(message = "숙련도는 필수 입력입니다.")
    @Size(min = 1, max = 20, message = "숙련도는 1자 이상, 20자 이하여야 합니다.")
    private String proficiency;     // 숙련도

    @NotNull(message = "사용 경험은 필수 입니다.")
    @Min(value = 0, message = "사용 경험은 0년 이상이어야 합니다.")
    private Integer yearsOfExp;      // 사용 경험(년, 0이상);
}
