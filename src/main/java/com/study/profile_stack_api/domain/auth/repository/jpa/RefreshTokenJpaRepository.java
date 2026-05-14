package com.study.profile_stack_api.domain.auth.repository.jpa;

import com.study.profile_stack_api.domain.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByMemberId(Long memberId);
    boolean existsByMemberId(Long memberId);

    @Transactional
    void deleteByMemberId(Long memberId);
}