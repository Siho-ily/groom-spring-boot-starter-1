package com.study.profile_stack_api.domain.profile.service;

import com.study.profile_stack_api.domain.profile.dto.request.ProfileCreateRequest;
import com.study.profile_stack_api.domain.profile.dto.request.ProfileUpdateRequest;
import com.study.profile_stack_api.domain.profile.dto.response.ProfileDeleteResponse;
import com.study.profile_stack_api.domain.profile.dto.response.ProfileResponse;
import com.study.profile_stack_api.domain.profile.entity.Position;
import com.study.profile_stack_api.domain.profile.entity.Profile;
import com.study.profile_stack_api.domain.profile.repository.dao.ProfileDao;
import com.study.profile_stack_api.global.common.Page;
import com.study.profile_stack_api.global.exception.domain.profile.ProfileNotFoundException;
import com.study.profile_stack_api.global.exception.validation.request.InvalidRequestValueException;
import com.study.profile_stack_api.global.exception.validation.request.NoUpdateRequestValueException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {
    // ProfileDao 인터페이스로 컨트롤
    private final ProfileDao repository;

    // === Create ===
    public ProfileResponse createProfile(ProfileCreateRequest request) {
        // 1. DTO -> Entity 변환
        Profile profile = Profile.builder()
                .memberId(request.getMemberId())
                .name(request.getName())
                .email(request.getEmail())
                .bio(request.getBio().isEmpty() ? "" : request.getBio())
                .position(Position.valueOf(request.getPosition()))
                .careerYears(request.getCareerYears())
                .githubUrl(request.getGithubUrl().isEmpty() ? "" : request.getGithubUrl())
                .blogUrl(request.getBlogUrl().isEmpty() ? "" : request.getBlogUrl())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 2. 레포지토리 저장
        Profile newProfile = repository.save(profile);

        // 3. Entity -> DTO 변환 후 리턴
        return ProfileResponse.from(newProfile);
    }

    // === Read ===
    public ProfileResponse getProfileById(Long id) {
        Optional<Profile> result = repository.findById(id);
        Profile profile = result.orElseThrow(() -> new ProfileNotFoundException(id));
        return ProfileResponse.from(profile);
    }

    public Page<ProfileResponse> getProfileWithPaging(Integer page, Integer size, String name, String position) {
        // page, size가 올바른지 확인
        if (page != null && page < 0) {
            throw new InvalidRequestValueException("page", page.toString());
        } else if (size != null && size < 0) {
            throw new InvalidRequestValueException("size", size.toString());
        }

        // 조회
        Page<Profile> profilePage = repository.findWithPage(page, size, name, position);
        List<ProfileResponse> content = profilePage.getContent().stream()
                .map(ProfileResponse::from)
                .toList();

        return new Page<>(content,
                    profilePage.getPage(),
                    profilePage.getSize(),
                    profilePage.getTotalElements(),
                    profilePage.getTotalPages(),
                    profilePage.isFirst(),
                    profilePage.isLast(),
                    profilePage.isHasPrevious(),
                    profilePage.isHasNext()
                );
    }

    // === Update ===
    public ProfileResponse updateProfileById(Long id, ProfileUpdateRequest request) {
        // 1. 수정할 사항이 있는지 확인
        if (request.hasNoUpdates()) {
            throw new NoUpdateRequestValueException();
        }

        // 2. 기존 프로필 조회
        Profile profile = repository.findById(id).orElseThrow(() -> new ProfileNotFoundException(id));

        // 3. 포지션 수정 있다면 변경
        Position position = null;
        if (request.getPosition() != null) {
            position = Position.valueOf(request.getPosition());
        }

        // 4. Entity 업데이트 (Profile)
        profile.update(request.getName(), request.getEmail(), request.getBio(), position,  request.getCareerYears(), request.getGithubUrl(), request.getBlogUrl());

        // 5. 저장 및 응답 반환
        repository.update(profile);
        return ProfileResponse.from(profile);
    }

    // === Delete ===
    public ProfileDeleteResponse deleteProfileById(Long id) {
        // 1. 프로필 존재 확인
        repository.findById(id).orElseThrow(() -> new ProfileNotFoundException(id));

        // 2. 삭제 수행
        repository.deleteById(id);

        // 3. 삭제 결과 반환
        return ProfileDeleteResponse.from(id);
    }
}
