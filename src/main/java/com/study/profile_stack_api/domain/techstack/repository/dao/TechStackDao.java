package com.study.profile_stack_api.domain.techstack.repository.dao;

import com.study.profile_stack_api.domain.techstack.entity.Proficiency;
import com.study.profile_stack_api.domain.techstack.entity.TechCategory;
import com.study.profile_stack_api.domain.techstack.entity.TechStack;
import com.study.profile_stack_api.global.common.Page;

import java.util.List;
import java.util.Optional;

public interface TechStackDao {
    // === Create ===
    TechStack create(Long profileId, TechStack techStack);

    // === Read ===
    Optional<TechStack> findById(Long profileId, Long techStackId);                 // id로 단건 조회

    Page<TechStack> findWithPage(Integer page, Integer size, Long profileId, TechCategory category, Proficiency proficiency);               // 페이징 조회

    // === Update ===
    TechStack update(Long profileId, Long techStackId, TechStack techStack);

    // === Delete ===
    boolean delete(Long techStackId);

    // === Utils ===
    long count(Long profileId, TechCategory category, Proficiency proficiency);                                             // 전체 데이터의 갯수 확인
    boolean existsById(Long techStackId);                                // profileId의 프로파일에 techStackId에 해당하는 데이터가 존재하는지 확인
}
