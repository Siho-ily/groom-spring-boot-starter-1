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
import com.study.profile_stack_api.global.exception.InvalidRequestField;
import com.study.profile_stack_api.global.exception.ProfileNotFoundException;
import com.study.profile_stack_api.global.exception.TechStackNotFoundException;
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
        TechStack techStack = repository.findById(profileId, techStackId)
                .orElseThrow(() -> new TechStackNotFoundException(techStackId));
        return TechStackResponse.from(techStack);
    }

    public Page<TechStackResponse> getTechStacksWithPage(Long profileId, int page, int limit) {
        Page<TechStack> techStackPage = repository.findWithPage(profileId, page, limit);
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
        TechStack techStack = new TechStack();
        techStack.setProfileId(profileId);
        techStack.setName(request.getName());
        techStack.setCategory(TechCategory.valueOf(request.getCategory()));
        techStack.setProficiency(Proficiency.valueOf(request.getProficiency()));
        techStack.setYearsOfExp(request.getYearsOfExp());
        techStack.setCreatedAt(LocalDateTime.now());
        techStack.setUpdatedAt(LocalDateTime.now());

        techStack = repository.create(profileId, techStack);

        return TechStackResponse.from(techStack);
    }

    // === PUT ===
    public TechStackResponse updateTechStack(Long profileId, Long techStackId, TechStackUpdateRequest request) {
        // validation 체크
        validationUpdateTechStackRequest(profileId, techStackId, request);

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

        return TechStackDeleteResponse.of(techStackId);
    }

    // === Utils ===
    // profileId 검증
    private void existsProfileId(Long profileId) {
        if (profileId != null && !profileDao.existsById(profileId)) {
            throw new ProfileNotFoundException(profileId);
        }
    }

    private void existsTechStackId(Long techStackId) {
        if (techStackId != null && repository.existsById(techStackId));
    }

    // update request 유효성 검사
    private void validationUpdateTechStackRequest(Long profileId, Long techStackId, TechStackUpdateRequest request) {
        if(request.hasNoUpdates()) {
            throw new InvalidRequestField("수정할 필드가 없습니다.");
        }

        existsProfileId(profileId);
        existsTechStackId(techStackId);

        if(request.getName() != null) {
            validationName(request.getName());
        }

        if(request.getCategory() != null) {
            validationCategory(TechCategory.valueOf(request.getCategory()));
        }

        if(request.getProficiency() != null) {
            validationProficiency(Proficiency.valueOf(request.getProficiency()));
        }

        if(request.getYearOfExp() != null) {
            validationYearsOfExp(request.getYearOfExp());
        }

        // name                 // 제약조건: 최대 50자
        // category             // 제약조건:
        // proficiency          // 제약조건:
        // yearsOfExp           // 제약조건: 최소 0
    }

    // validate
    private void validationName(String name) {
        if (name.length() < 50) {
            throw new InvalidRequestField("name", name);
        }
    }

    private void validationCategory(TechCategory category) {
        if (category == null) {

        }
    }

    private void validationProficiency(Proficiency proficiency) {

    }

    private void validationYearsOfExp(Integer yearsOfExp) {
        if (yearsOfExp < 0) {
            throw new InvalidRequestField("yearsOfExp", yearsOfExp.toString());
        }
    }
}
