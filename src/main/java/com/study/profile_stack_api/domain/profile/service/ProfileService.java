package com.study.profile_stack_api.domain.profile.service;

import com.study.profile_stack_api.domain.profile.dto.request.ProfileCreateRequest;
import com.study.profile_stack_api.domain.profile.dto.request.ProfileUpdateRequest;
import com.study.profile_stack_api.domain.profile.dto.response.ProfileDeleteResponse;
import com.study.profile_stack_api.domain.profile.dto.response.ProfileResponse;
import com.study.profile_stack_api.domain.profile.entity.Position;
import com.study.profile_stack_api.domain.profile.entity.Profile;
import com.study.profile_stack_api.domain.profile.repository.dao.ProfileDao;
import com.study.profile_stack_api.global.common.Page;
import com.study.profile_stack_api.global.exception.common.ErrorCode;
import com.study.profile_stack_api.global.exception.BusinessException;
import com.study.profile_stack_api.global.exception.domain.profile.ProfileNotFoundException;
import com.study.profile_stack_api.global.exception.validation.request.DuplicateEmailException;
import com.study.profile_stack_api.global.exception.validation.request.InvalidRequestValueException;
import com.study.profile_stack_api.global.exception.validation.request.NoUpdateRequestValueException;
import com.study.profile_stack_api.global.exception.validation.request.RequiredRequestValueException;
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
        // 1. 리퀘스트 바디 검증
        validateCreateProfile(request);

        // 2. DTO -> Entity 변환
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

        // 3. 레포지토리 저장
        Profile newProfile = repository.save(profile);

        // 4. Entity -> DTO 변환 후 리턴
        return ProfileResponse.from(newProfile);
    }

    // === Read ===
    public ProfileResponse getProfileById(Long id) {
        // 검증
        if (id == null || id <= 0) {
            throw new InvalidRequestValueException("id", Objects.requireNonNull(id).toString());
        }
        Optional<Profile> result = repository.findById(id);
        Profile profile = result.orElseThrow(() -> new ProfileNotFoundException(id));
        return ProfileResponse.from(profile);
    }

    public Page<ProfileResponse> getProfileWithPaging(Integer page, Integer size, String name, String position) {
        // 검증
        if (page != null && page < 0) {
            throw new InvalidRequestValueException("page", page.toString());
        } else if (size != null && size < 0) {
            throw new InvalidRequestValueException("size", size.toString());
        }
        if (name != null) validateName(name);
        if (position != null) validatePosition(position);

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

        // 2. 수정할 값들의 유효성 검증
        validateUpdateProfile(request);

        // 3. 기존 프로필 조회
        Profile profile = repository.findById(id).orElseThrow(() -> new ProfileNotFoundException(id));

        // 4. 포지션 수정 있다면 변경
        Position position = null;
        if (request.getPosition() != null) {
            position = Position.valueOf(request.getPosition());
        }

        // 5. Entity 업데이트 (Profile)
        profile.update(request.getName(), request.getEmail(), request.getBio(), position,  request.getCareerYears(), request.getGithubUrl(), request.getBlogUrl());

        // 6. 저장 및 응답 반환
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

    // ===============================================

    // === Validation ===
    private void validateCreateProfile(ProfileCreateRequest request) {
        // 이름: 필수, 1~50자
        if (request.getName() == null) {
            throw new RequiredRequestValueException("name");
        } else {
            validateName(request.getName());
        }

        // 이메일
        if (request.getEmail() == null) {
            throw new RequiredRequestValueException("email");
        } else {
            validateEmail(request.getEmail(), true);
        }

        // 바이오
        if (request.getBio() != null) {
            validateBio(request.getBio());
        }

        // 직무
        if (request.getPosition() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "직무는 필수 입력입니다.");
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
            throw new NoUpdateRequestValueException();
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
            throw new InvalidRequestValueException("이름은 입력으로 빈 문자열을 가질 수 없습니다.", "name", name);
        }

        if (name.length() > 50)
            throw new InvalidRequestValueException("이름은 1자 이상, 50자 이하여야 합니다.", "name", name);

    }

    private void validateEmail(String email, boolean checkExisted) {
        if (email.trim().isEmpty()) {
            throw new InvalidRequestValueException("이메일은 입력으로 빈 문자열을 가질 수 없습니다.", "email", email);
        }
        if (email.length() > 100) {
            throw new InvalidRequestValueException("이메일은 1자 이상, 100자 이하여야 합니다.", "email", email);
        }
        if (checkExisted && repository.existsByEmail(email)) {
            throw new DuplicateEmailException();
        }
    }


    private void validateBio(String bio) {
        if (bio != null && bio.length() > 500) {
            throw new InvalidRequestValueException("자기소개는 500자 이하여야 합니다.", "bio", bio);
        }
    }

    private void validatePosition(String position) {
        if (position.trim().isEmpty()) {
            throw new InvalidRequestValueException("position", position);
        }
        if (!Position.exists(position)) {
            throw new InvalidRequestValueException("해당 직무가 등록 되어있지 않습니다.", "position", position);
        }
    }

    private void validateCareerYears(Integer careerYears) {
        if (careerYears < 0) {
            throw new InvalidRequestValueException("경력 연차는 0 이상이어야 합니다.", "careerYears", careerYears.toString());
        }
    }

    private void validateGithubUrl(String githubUrl) {
        if (githubUrl.trim().isEmpty()) {
            throw new InvalidRequestValueException("깃허브 URL은 입력으로 빈 문자열을 가질 수 업습니다.", "githubUrl", githubUrl);
        }
    }

    private void validateBlogUrl(String blogUrl) {
        if (blogUrl.trim().isEmpty()) {
            throw new InvalidRequestValueException("블로그 URL은 입력으로 빈 문자열을 가질 수 업습니다.", "blogUrl", blogUrl);
        }
    }
}
