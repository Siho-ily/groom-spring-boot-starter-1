package com.study.profile_stack_api.domain.profile.controller;

import com.study.profile_stack_api.domain.auth.service.CustomUserDetails;
import com.study.profile_stack_api.domain.profile.dto.request.ProfileCreateRequest;
import com.study.profile_stack_api.domain.profile.dto.request.ProfileUpdateRequest;
import com.study.profile_stack_api.domain.profile.dto.response.ProfileDeleteResponse;
import com.study.profile_stack_api.domain.profile.dto.response.ProfileResponse;
import com.study.profile_stack_api.domain.profile.entity.Position;
import com.study.profile_stack_api.domain.profile.service.ProfileService;
import com.study.profile_stack_api.global.common.ApiResponse;
import com.study.profile_stack_api.global.common.Page;
import com.study.profile_stack_api.global.validation.annotation.NotBlankIfPresent;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
@Validated
public class ProfileController {
    private final ProfileService profileService;

    // === Profile API ===
    // GET
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(
            @PathVariable @Positive(message = "id는 양수여야 합니다.") Long id
    ) {
        // service를 호출해서, id를 통해 Profile을 가져오기
        ProfileResponse response = profileService.getProfileById(id);
        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProfileResponse>>> getProfilesWithPaging(
            @RequestParam(defaultValue = "0") @PositiveOrZero(message = "page는 양수여야 합니다.") Integer page,
            @RequestParam(defaultValue = "10") @Positive(message = "size는 1 이상이어야 합니다.") Integer size,
            @RequestParam(required = false) @NotBlankIfPresent(message = "name는 빈 문자열을 가질 수 없습니다.") String name,
            @RequestParam(required = false) Position position
    ) {
        // findAllWithPaging
        // service를 호출해서 page, size값을 전달하여 데이터 가져오기
        Page<ProfileResponse> responses = profileService.getProfileWithPaging(page, size, name, position);
        return ResponseEntity.ok().body(ApiResponse.success(responses));
    }

    // POST
    @PostMapping
    public ResponseEntity<ApiResponse<ProfileResponse>> createProfile(
            @RequestBody @Valid ProfileCreateRequest request

    ) {
        ProfileResponse response = profileService.createProfile(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
            @PathVariable @Positive(message = "id는 양수여야 합니다.") Long id,
            @RequestBody @Valid ProfileUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ProfileResponse response = profileService.updateProfileById(id, userDetails.getMemberId(), request);
        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<ProfileDeleteResponse>> deleteProfile(
            @PathVariable @Positive(message = "id는 양수여야 합니다.") Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ProfileDeleteResponse response = profileService.deleteProfileById(id, userDetails.getMemberId());
        return ResponseEntity.ok().body(ApiResponse.success(response));
    }
}
