package com.study.profile_stack_api.domain.profile.repository.jpa;

import com.study.profile_stack_api.domain.profile.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ProfileJpaRepository extends JpaRepository<Profile, Long>, JpaSpecificationExecutor<Profile> {
    Optional<Profile> findByMemberId(Long memberId);
    boolean existsByEmail(String email);
}