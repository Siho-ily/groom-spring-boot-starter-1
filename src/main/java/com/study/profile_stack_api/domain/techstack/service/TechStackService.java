package com.study.profile_stack_api.domain.techstack.service;

import com.study.profile_stack_api.domain.profile.dto.response.ProfileResponse;
import com.study.profile_stack_api.domain.techstack.dto.response.TechStackResponse;
import com.study.profile_stack_api.domain.techstack.entity.TechStack;
import com.study.profile_stack_api.domain.techstack.repository.dao.TechStackDao;
import com.study.profile_stack_api.global.common.Page;
import com.study.profile_stack_api.global.exception.TechStackNotFoundException;
import org.springframework.stereotype.Service;

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
        Page<TechStackResponse> techStackResponsePage = repository.findWithPage(profileId, page, limit);


        return techStackResponsePage;
    }

    // === POST ===

    // === PUT ===

    // === DELETE ===

    // Utils
}
