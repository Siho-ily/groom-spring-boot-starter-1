package com.study.profile_stack_api.domain.techstack.controller;

import com.study.profile_stack_api.domain.techstack.dto.request.TechStackCreateRequest;
import com.study.profile_stack_api.domain.techstack.dto.request.TechStackUpdateRequest;
import com.study.profile_stack_api.domain.techstack.dto.response.TechStackDeleteResponse;
import com.study.profile_stack_api.domain.techstack.dto.response.TechStackResponse;
import com.study.profile_stack_api.domain.techstack.service.TechStackService;
import com.study.profile_stack_api.global.common.ApiResponse;
import com.study.profile_stack_api.global.common.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profiles/{profileId}/tech-stacks")
public class TechStackController {
    private final TechStackService service;

    public TechStackController(TechStackService service) {this.service = service;}

    // GET
    @GetMapping("/{techStackId}")
    public ResponseEntity<ApiResponse<TechStackResponse>> getTechStack(@PathVariable Long profileId, @PathVariable Long techStackId) {
        TechStackResponse response = service.getTechStackById(profileId, techStackId);

        return ResponseEntity.ok().body(ApiResponse.success(
                response
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TechStackResponse>>> getTechStacksWithPage(@PathVariable Long profileId, @RequestParam Integer page, @RequestParam Integer size) {
        Page<TechStackResponse> response = service.getTechStacksWithPage(profileId,  page, size);
        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    // POST
    @PostMapping
    public ResponseEntity<ApiResponse<TechStackResponse>> addTechStack(@PathVariable Long profileId, @RequestBody TechStackCreateRequest request) {
        TechStackResponse response = service.createTechStack(profileId, request);
        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TechStackResponse>> updateTechStack(@PathVariable Long profileId, @PathVariable Long id, @RequestBody TechStackUpdateRequest request) {
        TechStackResponse response = service.updateTechStack(profileId, id, request);
        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<TechStackDeleteResponse>> deleteTechStack(@PathVariable Long profileId, @PathVariable Long id) {
        TechStackDeleteResponse response = service.deleteTechStack(id);
        return ResponseEntity.ok().body(ApiResponse.success(response));
    }
}
