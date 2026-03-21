package com.study.profile_stack_api.domain.profile.repository.dao;

import com.study.profile_stack_api.domain.profile.dto.response.ProfileResponse;
import com.study.profile_stack_api.domain.profile.entity.Position;
import com.study.profile_stack_api.domain.profile.entity.Profile;
import com.study.profile_stack_api.global.common.Page;

import java.util.List;
import java.util.Optional;

public interface ProfileDao {
    // DB가 수행해야 할 기능 정의
    // 메서드 시그니처 정의
    // === Create ===
    Profile save(Profile profile);                              // 프로필 생성

    // === Read ===
    Optional<Profile> findById(Long id);                        // 단건 조회
    Page<Profile> findWithPage(Integer offset, Integer limit, String name, Position position);          // 페이징 조회

    // === Update ===
    Profile update(Profile profile);                  // 프로필 수정

    // === Delete ===
    boolean deleteById(Long id);                                // 프로필 삭제


    // utils
    long count(String name, Position position);                                               // 전체 데이터의 갯수 확인
    boolean existsById(Long id);                                // id에 해당하는 데이터가 존재하는지 확인
    boolean existsByEmail(String email);                        // email에 해당하는 데이터가 존재하는지 확인(email 중복 체크)

}
