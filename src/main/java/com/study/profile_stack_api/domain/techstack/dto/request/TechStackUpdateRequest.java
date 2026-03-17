package com.study.profile_stack_api.domain.techstack.dto.request;

import com.study.profile_stack_api.global.validation.annotation.NotBlankIfPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class TechStackUpdateRequest {
    @NotBlankIfPresent(message = "기술명은 빈 값을 가질 수 없습니다.")
    @Size(min = 1, max = 50, message = "기술명은 1자 이상, 50자 이하여야 합니다.")
    private String name;            // 기술명 (1 ~ 50자)

    @NotBlankIfPresent(message = "기술명은 빈 값을 가질 수 없습니다.")
    @Size(min = 1, max = 20, message = "기술 카테고리는 1자 이상, 20자 이하여야 합니다.")
    private String category;        // 기술 카테고리

    @NotBlankIfPresent(message = "기술명은 빈 값을 가질 수 없습니다.")
    @Size(min = 1, max = 20, message = "숙련도는 1자 이상, 20자 이하여야 합니다.")
    private String proficiency;     // 숙련도

    @Min(value = 1, message = "사용 경험은 1년 이상, 100년 이하여야 합니다.")
    @Max(value = 100, message = "사용 경험은 1년 이상, 100년 이하여야 합니다.")
    private Integer yearOfExp;      // 사용 경험(년, 0이상);

    public boolean hasNoUpdates() {
        return name == null
                && category == null
                && proficiency == null
                && yearOfExp == null;
    }
}
