package com.study.profile_stack_api.domain.techstack.repository.dao;

import com.study.profile_stack_api.domain.techstack.entity.TechStack;
import com.study.profile_stack_api.global.common.Page;

import java.util.Optional;

public interface TechStackDao {
    // === Create ===
    TechStack create(Long profileId, TechStack techStack);

    // === Read ===
    Optional<TechStack> findById(Long profileId, Long techStackId);                 // id로 단건 조회
    Page<TechStack> findWithPage(int offset, int limit);                            // 페이징 조회
    Page<TechStack> findByCategory(int page, int limit, String category);           // 페이징 조회 - 카테고리 필터링
    Page<TechStack> findByProficiency(int page, int limit, String proficiency);     // 페이징 조회 - 숙련도 필터링

    // === Update ===
    TechStack update(Long profileId, Long techStackId, TechStack techStack);

    // === Delete ===
    boolean delete(Long profileId, Long techStackId);

    // === Utils ===
    long count();                                               // 전체 데이터의 갯수 확인
    boolean existsById(Long id);                                // id에 해당하는 데이터가 존재하는지 확인
}
