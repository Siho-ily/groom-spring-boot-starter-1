package com.study.profile_stack_api.domain.techstack.service;

import com.study.profile_stack_api.domain.profile.entity.Profile;
import com.study.profile_stack_api.domain.profile.repository.dao.ProfileDao;
import com.study.profile_stack_api.domain.techstack.dto.request.TechStackCreateRequest;
import com.study.profile_stack_api.domain.techstack.dto.request.TechStackUpdateRequest;
import com.study.profile_stack_api.domain.techstack.dto.response.TechStackDeleteResponse;
import com.study.profile_stack_api.domain.techstack.dto.response.TechStackResponse;
import com.study.profile_stack_api.domain.techstack.entity.Proficiency;
import com.study.profile_stack_api.domain.techstack.entity.TechCategory;
import com.study.profile_stack_api.domain.techstack.entity.TechStack;
import com.study.profile_stack_api.domain.techstack.mapper.TechStackMapper;
import com.study.profile_stack_api.domain.techstack.repository.dao.TechStackDao;
import com.study.profile_stack_api.global.common.Page;
import com.study.profile_stack_api.global.exception.domain.auth.ForbiddenOwnerMismatch;
import com.study.profile_stack_api.global.exception.validation.request.InvalidRequestValueException;
import com.study.profile_stack_api.global.exception.validation.request.NoUpdateRequestValueException;
import com.study.profile_stack_api.global.exception.domain.profile.ProfileNotFoundException;
import com.study.profile_stack_api.global.exception.domain.techstack.TechStackNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TechStackService {
    private final TechStackMapper mapper;
    private final TechStackDao repository;
    private final ProfileDao profileDao;

    // === GET ===
    public TechStackResponse getTechStackById(Long profileId, Long techStackId) {
        if (!profileDao.existsById(profileId)) {
            throw new ProfileNotFoundException(profileId);
        }

        TechStack techStack = repository.findById(profileId, techStackId)
                .orElseThrow(() -> new TechStackNotFoundException(techStackId));
        return mapper.toResponse(techStack);
    }

    public Page<TechStackResponse> getTechStacksWithPage(Integer page, Integer limit, Long profileId, TechCategory category, Proficiency proficiency) {
        // validation
        if (!profileDao.existsById(profileId)) {
            throw new ProfileNotFoundException(profileId);
        }
        if (page != null && page < 0) {
            throw new InvalidRequestValueException("page", page.toString());
        }
        if (limit != null && limit <= 0) {
            throw new InvalidRequestValueException("size", limit.toString());
        }

        Page<TechStack> techStackPage = repository.findWithPage(page, limit, profileId, category, proficiency);
        List<TechStackResponse> content = mapper.toResponseList(techStackPage.getContent());
        return new Page<>(
                content,
                techStackPage.getPage(),
                techStackPage.getSize(),
                techStackPage.getTotalElements(),
                techStackPage.getTotalPages(),
                techStackPage.isFirst(),
                techStackPage.isLast(),
                techStackPage.isHasPrevious(),
                techStackPage.isHasNext()
        );
    }

    // === POST ===
    public TechStackResponse createTechStack(Long profileId, Long memberId, TechStackCreateRequest request) {
        verifyOwnership(profileId, memberId);

        TechStack techStack = mapper.toEntity(request);
        techStack.setProfileId(profileId);

        return mapper.toResponse(repository.create(profileId, techStack));
    }

    // === PUT ===
    public TechStackResponse updateTechStack(Long profileId, Long memberId, Long techStackId, TechStackUpdateRequest request) {
        if(request.hasNoUpdates()) {
            throw new NoUpdateRequestValueException();
        }

        verifyOwnership(profileId, memberId);

        TechStack entity = repository.findById(profileId, techStackId)
                .orElseThrow(() -> new TechStackNotFoundException(techStackId));

        mapper.updateEntity(request, entity);

        return mapper.toResponse(repository.update(profileId, techStackId, entity));
    }

    // === DELETE ===
    public TechStackDeleteResponse deleteTechStack(Long profileId, Long memberId, Long techStackId) {
        verifyOwnership(profileId, memberId);
        repository.delete(profileId, techStackId);

        return TechStackDeleteResponse.from(techStackId);
    }

    // === Utils ===
    private Profile findProfile(Long profileId) {
        return profileDao.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(profileId));
    }

    private void verifyOwnership(Long profileId, Long memberId) {
        Profile profile = findProfile(profileId);
        if (!profile.getMemberId().equals(memberId)) {
            throw new ForbiddenOwnerMismatch();
        }
    }
}
