package com.study.profile_stack_api.domain.techstack.controller;

import com.study.profile_stack_api.domain.techstack.dto.request.TechStackCreateRequest;
import com.study.profile_stack_api.domain.techstack.dto.request.TechStackUpdateRequest;
import com.study.profile_stack_api.domain.techstack.dto.response.TechStackDeleteResponse;
import com.study.profile_stack_api.domain.techstack.dto.response.TechStackResponse;
import com.study.profile_stack_api.domain.techstack.entity.Proficiency;
import com.study.profile_stack_api.domain.techstack.entity.TechCategory;
import com.study.profile_stack_api.domain.techstack.service.TechStackService;
import com.study.profile_stack_api.global.common.ApiResponse;
import com.study.profile_stack_api.global.common.Page;
import com.study.profile_stack_api.global.validation.annotation.NotBlankIfPresent;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profiles/{profileId}/tech-stacks")
@RequiredArgsConstructor
@Validated
public class TechStackController {
    private final TechStackService service;

    // GET
    @GetMapping("/{techStackId}")
    public ResponseEntity<ApiResponse<TechStackResponse>> getTechStack(
            @PathVariable @Positive(message = "profileId는 양수여야 합니다.") Long profileId,
            @PathVariable @Positive(message = "techStackId는 양수여야 합니다.") Long techStackId
    ) {
        TechStackResponse response = service.getTechStackById(profileId, techStackId);

        return ResponseEntity.ok().body(ApiResponse.success(
                response
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TechStackResponse>>> getTechStacksWithPage(
            @PathVariable @Positive(message = "profileId는 양수여야 합니다.") Long profileId,
            @RequestParam(defaultValue = "0") @PositiveOrZero(message = "page는 0 이상이어야 합니다.") Integer page,
            @RequestParam(defaultValue = "0") @PositiveOrZero(message = "size는 0 이상이어야 합니다.") Integer size,
            @RequestParam(required = false) TechCategory category,
            @RequestParam(required = false) Proficiency proficiency
    ) {
        Page<TechStackResponse> response = service.getTechStacksWithPage(page, size, profileId, category, proficiency);
        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    // POST
    @PostMapping
    public ResponseEntity<ApiResponse<TechStackResponse>> addTechStack(
            @PathVariable @Positive(message = "profileId는 양수여야 합니다.") Long profileId,
            @Valid @RequestBody TechStackCreateRequest request
    ) {
        TechStackResponse response = service.createTechStack(profileId, request);
        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TechStackResponse>> updateTechStack(
            @PathVariable @Positive(message = "profileId는 양수여야 합니다.") Long profileId,
            @PathVariable @Positive(message = "id는 양수여야 합니다.") Long id,
            @Valid @RequestBody TechStackUpdateRequest request
    ) {
        TechStackResponse response = service.updateTechStack(profileId, id, request);
        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<TechStackDeleteResponse>> deleteTechStack(
            @PathVariable @Positive(message = "profileId는 양수여야 합니다.") Long profileId,
            @PathVariable @Positive(message = "id는 양수여야 합니다.") Long id
    ) {
        TechStackDeleteResponse response = service.deleteTechStack(id);
        return ResponseEntity.ok().body(ApiResponse.success(response));
    }
}
