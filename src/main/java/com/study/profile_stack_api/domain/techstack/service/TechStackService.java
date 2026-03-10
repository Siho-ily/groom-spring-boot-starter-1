package com.study.profile_stack_api.domain.techstack.service;

import com.study.profile_stack_api.domain.profile.dto.response.ProfileResponse;
import com.study.profile_stack_api.domain.techstack.dto.request.TechStackCreateRequest;
import com.study.profile_stack_api.domain.techstack.dto.response.TechStackResponse;
import com.study.profile_stack_api.domain.techstack.entity.Proficiency;
import com.study.profile_stack_api.domain.techstack.entity.TechCategory;
import com.study.profile_stack_api.domain.techstack.entity.TechStack;
import com.study.profile_stack_api.domain.techstack.repository.dao.TechStackDao;
import com.study.profile_stack_api.global.common.Page;
import com.study.profile_stack_api.global.exception.TechStackNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TechStackService {
    private final TechStackDao repository;

    public TechStackService(TechStackDao repository) {
        this.repository = repository;
    }

    // === GET ===
    public TechStackResponse getTechStackById(Long profileId, Long techStackId) {
        TechStack techStack = repository.findById(profileId, techStackId)
                .orElseThrow(() -> new TechStackNotFoundException(techStackId));
        return TechStackResponse.from(techStack);
    }

    public Page<TechStackResponse> getTechStacksWithPage(Long profileId, int page, int limit) {
        return repository.findWithPage(profileId, page, limit);
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

    // === DELETE ===

    // Utils
}
