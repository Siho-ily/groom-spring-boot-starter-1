package com.study.profile_stack_api.domain.profile.service;

import com.study.profile_stack_api.domain.profile.dto.request.ProfileCreateRequest;
import com.study.profile_stack_api.domain.profile.dto.request.ProfileUpdateRequest;
import com.study.profile_stack_api.domain.profile.dto.response.ProfileDeleteResponse;
import com.study.profile_stack_api.domain.profile.dto.response.ProfileResponse;
import com.study.profile_stack_api.domain.profile.entity.Position;
import com.study.profile_stack_api.domain.profile.entity.Profile;
import com.study.profile_stack_api.domain.profile.repository.dao.ProfileDao;
import com.study.profile_stack_api.global.common.Page;
import com.study.profile_stack_api.global.exception.ApiException;
import com.study.profile_stack_api.global.exception.ErrorCode;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProfileService {
    // ProfileDao 인터페이스로 컨트롤
    private final ProfileDao repository;
    // IoC에 의한 종속성 주입
    public ProfileService(ProfileDao profileDao) {this.repository = profileDao;}

    // === Create ===
    public ProfileResponse createProfile(ProfileCreateRequest request) {
        // 1. 리퀘스트 바디 검증
        validateCreateProfile(request);

        // 2. DTO -> Entity 변환
        Profile profile = new Profile(
                null,
                request.getName().trim(),
                request.getEmail().trim(),
                request.getBio() != null ? request.getBio().trim() : null,
                Position.valueOf(request.getPosition()),
                request.getCareerYears(),
                request.getGithubUrl() != null ? request.getGithubUrl().trim() : null,
                request.getBlogUrl() != null ? request.getBlogUrl().trim() : null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        // 3. 레포지토리 저장
        Profile newProfile = repository.save(profile);

        // 4. Entity -> DTO 변환 후 리턴
        return ProfileResponse.from(newProfile);
    }

    // === Read ===
    public ProfileResponse getProfileById(Long id) {
        // 검증
        if (id == null || id <= 0) {
            throw new ApiException(ErrorCode.INVALID_INPUT, "잘못된 id 값입니다.");
        }
        Optional<Profile> result = repository.findById(id);
        Profile profile = result.orElseThrow(() -> new ApiException(ErrorCode.PROFILE_NOT_FOUND));
        return ProfileResponse.from(profile);
    }

    public Page<ProfileResponse> getProfileWithPaging(Integer page, Integer size) {
        // 검증
        if (page == null || page < 0 || size == null || size <= 0) {
            throw new ApiException(ErrorCode.INVALID_INPUT, "잘못된 입력 값입니다. page: %d, size: %d".formatted(page, size));
        }

        return repository.findWithPage(page, size);
    }

    // === Update ===
    public ProfileResponse updateProfileById(Long id, ProfileUpdateRequest request) {
        // 1. 수정할 사항이 있는지 확인
        if (request.hasNoUpdates()) {
            throw new ApiException(ErrorCode.INVALID_INPUT, "수정할 항목이 없습니다.");
        }

        // 2. 수정할 값들의 유효성 검증
        validateUpdateProfile(request);

        // 3. 기존 프로필 조회
        Profile profile = repository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.PROFILE_NOT_FOUND));

        // 4. 포지션 수정 있다면 변경
        Position position = null;
        if (request.getPosition() != null) {
            position = Position.valueOf(request.getPosition());
        }

        // 5. Entity 업데이트 (Profile)
        profile.update(null, request.getName(), request.getEmail(), request.getBio(), position,  request.getCareerYears(), request.getGithubUrl(), request.getBlogUrl());

        // 6. 저장 및 응답 반환
        repository.update(profile);
        return ProfileResponse.from(profile);
    }

    // === Delete ===
    public ProfileDeleteResponse deleteProfileById(Long id) {
        // 1. 프로필 존재 확인
        repository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.PROFILE_NOT_FOUND));

        // 2. 삭제 수행
        repository.deleteById(id);

        // 3. 삭제 결과 반환
        return ProfileDeleteResponse.of(id);
    }

    // ===============================================

    // === Validation ===
    private void validateCreateProfile(ProfileCreateRequest request) {
        // 이름: 필수, 1~50자
        if (request.getName() == null) {
            throw new ApiException(ErrorCode.INVALID_INPUT, "이름은 필수 입력입니다.");
        } else {
            validateName(request.getName());
        }

        // 이메일
        if (request.getEmail() == null) {
            throw new ApiException(ErrorCode.INVALID_INPUT, "이메일은 필수 입력입니다.");
        } else {
            validateEmail(request.getEmail(), true);
        }

        // 바이오
        if (request.getBio() != null) {
            validateBio(request.getBio());
        }

        // 직무
        if (request.getPosition() == null) {
            throw new ApiException(ErrorCode.INVALID_INPUT, "직무는 필수 입력입니다.");
        } else {
            validatePosition(request.getPosition());
        }

        // 경력 연차
        if (request.getCareerYears() != null) {
            validateCareerYears(request.getCareerYears());
        }

        // 깃허브 링크
        if (request.getGithubUrl() != null) {
            validateGithubUrl(request.getGithubUrl());
        }

        // 블로그 링크
        if (request.getBlogUrl() != null) {
            validateBlogUrl(request.getBlogUrl());
        }
    }

    private void validateUpdateProfile(ProfileUpdateRequest request) {
        if (request.hasNoUpdates()) {
            throw new ApiException(ErrorCode.INVALID_INPUT, "수정할 목록이 없습니다.");
        }

        // 이름
        if (request.getName() != null) {
            validateName(request.getName());
        }

        // 이메일
        if (request.getEmail() != null) {
            validateEmail(request.getEmail(), false);
        }

        // 바이오
        if (request.getBio() != null) {
            validateBio(request.getBio());
        }

        // 직무
        if (request.getPosition() != null) {
            validatePosition(request.getPosition());
        }

        // 경력 연차
        if (request.getCareerYears() != null) {
            validateCareerYears(request.getCareerYears());
        }

        // 깃허브 주소
        if (request.getGithubUrl() != null) {
            validateGithubUrl(request.getGithubUrl());
        }

        // 블로그 주소
        if (request.getBlogUrl() != null) {
            validateBlogUrl(request.getBlogUrl());
        }
    }

    // === validation base ===
    private void validateName(String name) {
        if (name.trim().isEmpty()) {
            throw new ApiException(ErrorCode.INVALID_INPUT, "이름은 입력으로 빈 문자열을 가질 수 없습니다.");
        }

        if (name.length() > 50)
            throw new ApiException(ErrorCode.INVALID_INPUT, "이름은 1자 이상, 50자 이하여야 합니다.");

    }

    private void validateEmail(String email, boolean checkExisted) {
        if (email.trim().isEmpty()) {
            throw new ApiException(ErrorCode.INVALID_INPUT, "이메일은 입력으로 빈 문자열을 가질 수 없습니다.");
        }
        if (email.length() > 100) {
            throw new ApiException(ErrorCode.INVALID_INPUT, "이메일은 1자 이상, 100자 이하여야 합니다.");
        }
        if (checkExisted && repository.existsByEmail(email)) {
            throw new ApiException(ErrorCode.DUPLICATE_EMAIL, "이미 존재하는 이메일 주소입니다.");
        }
    }

    private void validateBio(String bio) {
        if (bio != null && bio.length() > 500) {
            throw new ApiException(ErrorCode.INVALID_INPUT, "자기소개는 500자 이하여야 합니다.");
        }
    }

    private void validatePosition(String position) {
        if (position.trim().isEmpty()) {
            throw new ApiException(ErrorCode.INVALID_INPUT, "포지션은 입력으로 빈 문자열을 가질 수 없습니다.");
        }
        try {
            Position.valueOf(position);
        } catch (IllegalArgumentException e) {
            throw new ApiException(ErrorCode.INVALID_INPUT, "정의되지 않은 직무 입력입니다.");
        }
    }

    private void validateCareerYears(Integer careerYears) {
        if (careerYears < 0) {
            throw new ApiException(ErrorCode.INVALID_INPUT, "경력 연차는 0 이상이어야 합니다.");
        }
    }

    private void validateGithubUrl(String githubUrl) {
        if (githubUrl.trim().isEmpty()) {
            throw new ApiException(ErrorCode.INVALID_INPUT, "깃허브 URL은 입력으로 빈 문자열을 가질 수 업습니다.");
        }
    }

    private void validateBlogUrl(String blogUrl) {
        if (blogUrl.trim().isEmpty()) {
            throw new ApiException(ErrorCode.INVALID_INPUT, "블로그 URL은 입력으로 빈 문자열을 가질 수 업습니다.");
        }
    }
}
