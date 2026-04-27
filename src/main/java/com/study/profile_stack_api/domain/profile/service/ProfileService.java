package com.study.profile_stack_api.domain.profile.service;

import com.study.profile_stack_api.domain.profile.dto.request.ProfileCreateRequest;
import com.study.profile_stack_api.domain.profile.dto.request.ProfileUpdateRequest;
import com.study.profile_stack_api.domain.profile.dto.response.ProfileDeleteResponse;
import com.study.profile_stack_api.domain.profile.dto.response.ProfileResponse;
import com.study.profile_stack_api.domain.profile.entity.Position;
import com.study.profile_stack_api.domain.profile.entity.Profile;
import com.study.profile_stack_api.domain.profile.mapper.ProfileMapper;
import com.study.profile_stack_api.domain.profile.repository.dao.ProfileDao;
import com.study.profile_stack_api.global.common.Page;
import com.study.profile_stack_api.global.exception.domain.auth.ForbiddenOwnerMismatch;
import com.study.profile_stack_api.global.exception.domain.profile.ProfileNotFoundException;
import com.study.profile_stack_api.global.exception.validation.request.InvalidRequestValueException;
import com.study.profile_stack_api.global.exception.validation.request.NoUpdateRequestValueException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {
    // ProfileDao 인터페이스로 컨트롤
    private final ProfileDao repository;
    private final ProfileMapper mapper;

    // === Create ===
    @Transactional
    public ProfileResponse createProfile(ProfileCreateRequest request) {
        // 1. DTO -> Entity 변환
        Profile profile = mapper.toEntity(request);

        // 2. 레포지토리 저장
        Profile newProfile = repository.save(profile);

        // 3. Entity -> DTO 변환 후 리턴
        return mapper.toResponse(newProfile);
    }

    // === Read ===
    @Transactional(readOnly = true)
    public ProfileResponse getProfileById(Long id) {
        Optional<Profile> result = repository.findById(id);
        Profile profile = result.orElseThrow(() -> new ProfileNotFoundException(id));

        return mapper.toResponse(profile);
    }

    @Transactional(readOnly = true)
    public Page<ProfileResponse> getProfileWithPaging(Integer page, Integer size, String name, Position position) {
        // page, size가 올바른지 확인
        if (page != null && page < 0) {
            throw new InvalidRequestValueException("page", page.toString());
        } else if (size != null && size <= 0) {
            throw new InvalidRequestValueException("size", size.toString());
        }

        // 조회
        Page<Profile> profilePage = repository.findWithPage(page, size, name, position);
        List<ProfileResponse> content = mapper.toResponseList(profilePage.getContent());

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
    @Transactional
    public ProfileResponse updateProfileById(
            Long id,
            Long memberId,
            ProfileUpdateRequest request
    ) {
        // 1. 수정할 사항이 있는지 확인
        if (request.hasNoUpdates()) {
            throw new NoUpdateRequestValueException();
        }

        // 2. 기존 프로필 조회
        Profile profile = repository.findById(id).orElseThrow(() -> new ProfileNotFoundException(id));

        // 소유권 검증
        if (!profile.getMemberId().equals(memberId)) {
            throw new ForbiddenOwnerMismatch();
        }

        // 3. Entity 업데이트 (Profile)
        mapper.updateEntity(request, profile);

        // 4. 저장 및 응답 반환
        repository.update(profile);
        return mapper.toResponse(profile);
    }

    // === Delete ===
    @Transactional
    public ProfileDeleteResponse deleteProfileById(Long id,  Long memberId) {
        // 1. 프로필 존재 확인
        Profile profile = repository.findById(id).orElseThrow(() -> new ProfileNotFoundException(id));

        // 소유권 검증
        if (!profile.getMemberId().equals(memberId)) {
            throw new ForbiddenOwnerMismatch();
        }

        // 2. 삭제 수행
        repository.deleteById(id);

        // 3. 삭제 결과 반환
        return ProfileDeleteResponse.from(id);
    }
}
