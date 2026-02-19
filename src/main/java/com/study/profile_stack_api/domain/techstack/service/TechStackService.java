package com.study.profile_stack_api.domain.techstack.service;

import com.study.profile_stack_api.domain.techstack.repository.dao.TechStackDao;
import org.springframework.stereotype.Service;

@Service
public class TechStackService {
    private TechStackDao repository;

    public TechStackService(TechStackDao repository) {
        this.repository = repository;
    }
}
