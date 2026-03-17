package com.study.profile_stack_api.domain.techstack.controller;

import com.study.profile_stack_api.domain.techstack.dto.request.TechStackCreateRequest;
import com.study.profile_stack_api.domain.techstack.dto.request.TechStackUpdateRequest;
import com.study.profile_stack_api.domain.techstack.dto.response.TechStackDeleteResponse;
import com.study.profile_stack_api.domain.techstack.dto.response.TechStackResponse;
import com.study.profile_stack_api.domain.techstack.service.TechStackService;
import com.study.profile_stack_api.global.common.ApiResponse;
import com.study.profile_stack_api.global.common.Page;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profiles/{profileId}/tech-stacks")
@RequiredArgsConstructor
public class TechStackController {
    private final TechStackService service;

    // GET
    @GetMapping("/{techStackId}")
    public ResponseEntity<ApiResponse<TechStackResponse>> getTechStack(@PathVariable Long profileId, @PathVariable Long techStackId) {
        TechStackResponse response = service.getTechStackById(profileId, techStackId);

        return ResponseEntity.ok().body(ApiResponse.success(
                response
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TechStackResponse>>> getTechStacksWithPage
            (@PathVariable Long profileId,
             @RequestParam Integer page,
             @RequestParam Integer size,
             @Nullable @RequestParam String category,
             @Nullable @RequestParam String proficiency) {
        Page<TechStackResponse> response = service.getTechStacksWithPage(page, size, profileId, category, proficiency);
        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    // POST
    @PostMapping
    public ResponseEntity<ApiResponse<TechStackResponse>> addTechStack(@PathVariable Long profileId, @Valid @RequestBody TechStackCreateRequest request) {
        TechStackResponse response = service.createTechStack(profileId, request);
        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TechStackResponse>> updateTechStack(@PathVariable Long profileId, @PathVariable Long id, @Valid @RequestBody TechStackUpdateRequest request) {
        TechStackResponse response = service.updateTechStack(profileId, id, request);
        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<TechStackDeleteResponse>> deleteTechStack(@PathVariable Long id) {
        TechStackDeleteResponse response = service.deleteTechStack(id);
        return ResponseEntity.ok().body(ApiResponse.success(response));
    }
}
