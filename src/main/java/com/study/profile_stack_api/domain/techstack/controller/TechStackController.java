package com.study.profile_stack_api.domain.techstack.controller;

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
    public ResponseEntity<ApiResponse<String>> addTechStack(@PathVariable Long profileId) {
        return ResponseEntity.ok().body(ApiResponse.success(
                "addTechStack | profileId: %s".formatted(profileId)
        ));
    }

    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateTechStack(@PathVariable Long profileId, @PathVariable String id) {
        return ResponseEntity.ok().body(ApiResponse.success(
                "updateTechStack | profileId: %s, id: %s".formatted(profileId, id)
        ));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteTechStack(@PathVariable Long profileId, @PathVariable String id) {
        return ResponseEntity.ok().body(ApiResponse.success(
                "deleteTechStack | profileId: %s, id: %s".formatted(profileId, id)
        ));
    }
}
