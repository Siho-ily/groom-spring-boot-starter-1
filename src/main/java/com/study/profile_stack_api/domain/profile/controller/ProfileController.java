package com.study.profile_stack_api.domain.profile.controller;

import com.study.profile_stack_api.domain.profile.dto.request.ProfileCreateRequest;
import com.study.profile_stack_api.domain.profile.dto.request.ProfileUpdateRequest;
import com.study.profile_stack_api.domain.profile.dto.response.ProfileDeleteResponse;
import com.study.profile_stack_api.domain.profile.dto.response.ProfileResponse;
import com.study.profile_stack_api.domain.profile.service.ProfileService;
import com.study.profile_stack_api.global.common.ApiResponse;
import com.study.profile_stack_api.global.common.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    // === Profile API ===
    // GET
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(@PathVariable Long id) {
        // service를 호출해서, id를 통해 Profile을 가져오기
        ProfileResponse response = profileService.getProfileById(id);
        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProfileResponse>>> getProfilesWithPaging(@RequestParam Integer page, @RequestParam Integer size) {
        // service를 호출해서 page, size값을 전달하여 데이터 가져오기
        Page<ProfileResponse> responses = profileService.getProfileWithPaging(page, size);
        return ResponseEntity.ok().body(ApiResponse.success(responses));
    }

    // POST
    @PostMapping
    public ResponseEntity<ApiResponse<ProfileResponse>> createProfile(@RequestBody ProfileCreateRequest request) {
        ProfileResponse response = profileService.createProfile(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(@PathVariable Long id,  @RequestBody ProfileUpdateRequest request) {
        ProfileResponse response = profileService.updateProfileById(id, request);
        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<ProfileDeleteResponse>> deleteProfile(@PathVariable Long id) {
        ProfileDeleteResponse response = profileService.deleteProfileById(id);
        return ResponseEntity.ok().body(ApiResponse.success(response));
    }
}
