package com.study.profile_stack_api.domain.techstack.repository.jpa;

import com.study.profile_stack_api.domain.techstack.entity.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TechStackJpaRepository extends JpaRepository<TechStack, Long>, JpaSpecificationExecutor<TechStack> {
    Optional<TechStack> findByProfileIdAndId(Long profileId, Long id);
    boolean existsByProfileIdAndId(Long profileId, Long id);
}