package com.study.profile_stack_api.domain.techstack.service;

import com.study.profile_stack_api.domain.profile.repository.dao.ProfileDao;
import com.study.profile_stack_api.domain.techstack.dto.request.TechStackCreateRequest;
import com.study.profile_stack_api.domain.techstack.dto.request.TechStackUpdateRequest;
import com.study.profile_stack_api.domain.techstack.dto.response.TechStackDeleteResponse;
import com.study.profile_stack_api.domain.techstack.dto.response.TechStackResponse;
import com.study.profile_stack_api.domain.techstack.entity.Proficiency;
import com.study.profile_stack_api.domain.techstack.entity.TechCategory;
import com.study.profile_stack_api.domain.techstack.entity.TechStack;
import com.study.profile_stack_api.domain.techstack.repository.dao.TechStackDao;
import com.study.profile_stack_api.global.common.Page;
import com.study.profile_stack_api.global.exception.validation.request.NoUpdateRequestValueException;
import com.study.profile_stack_api.global.exception.domain.profile.ProfileNotFoundException;
import com.study.profile_stack_api.global.exception.domain.techstack.TechStackNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TechStackService {
    private final TechStackDao repository;
    private final ProfileDao profileDao;

    // === GET ===
    public TechStackResponse getTechStackById(Long profileId, Long techStackId) {
        existsProfileId(profileId);
        existsTechStackId(techStackId);

        TechStack techStack = repository.findById(profileId, techStackId)
                .orElseThrow(() -> new TechStackNotFoundException(techStackId));
        return TechStackResponse.from(techStack);
    }

    public Page<TechStackResponse> getTechStacksWithPage(Integer page, Integer limit, Long profileId, TechCategory category, Proficiency proficiency) {
        // validation
        existsProfileId(profileId);

        Page<TechStack> techStackPage = repository.findWithPage(page, limit, profileId, category, proficiency);
        List<TechStackResponse> content = techStackPage.getContent().stream()
                .map(TechStackResponse::from)
                .toList();
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
    public TechStackResponse createTechStack(Long profileId, TechStackCreateRequest request) {
        existsProfileId(profileId);

        TechStack techStack = TechStack.builder()
                .profileId(profileId)
                .name(request.getName())
                .category(TechCategory.valueOf(request.getCategory()))
                .proficiency(Proficiency.valueOf(request.getProficiency()))
                .yearsOfExp(request.getYearsOfExp())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return TechStackResponse.from(repository.create(profileId, techStack));
    }

    // === PUT ===
    public TechStackResponse updateTechStack(Long profileId, Long techStackId, TechStackUpdateRequest request) {
        if(request.hasNoUpdates()) {
            throw new NoUpdateRequestValueException();
        }

        // validation 체크
        existsProfileId(profileId);
        existsTechStackId(techStackId);

        // DTO -> Entity
        TechStack entity = repository.findById(profileId, techStackId)
                .orElseThrow(() -> new TechStackNotFoundException(techStackId));

        // Update할 필드 저장
        TechCategory category = request.getCategory() != null
                ? TechCategory.valueOf(request.getCategory())
                : null;
        Proficiency proficiency = request.getProficiency() != null
                ? Proficiency.valueOf(request.getProficiency())
                : null;
        entity.update(
                request.getName(),
                category,
                proficiency,
                request.getYearOfExp()
        );

        // DAO를 통해 반영 및 응답
        return TechStackResponse.from(repository.update(profileId, techStackId, entity));
    }

    // === DELETE ===
    public TechStackDeleteResponse deleteTechStack(Long techStackId) {
        repository.delete(techStackId);

        return TechStackDeleteResponse.from(techStackId);
    }

    // === Utils ===
    // profileId 검증
    private void existsProfileId(Long profileId) {
        if (profileId != null && !profileDao.existsById(profileId)) {
            throw new ProfileNotFoundException(profileId);
        }
    }

    private void existsTechStackId(Long techStackId) {
        if (techStackId != null && !repository.existsById(techStackId)) {
            throw new TechStackNotFoundException(techStackId);
        }
    }
}
