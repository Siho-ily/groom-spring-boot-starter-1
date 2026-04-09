package com.study.profile_stack_api.domain.auth.repository.dao;

import com.study.profile_stack_api.domain.auth.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenDao {
    //`save`, `findByMemberId`, `deleteByMemberId`

    // Create
    Optional<RefreshToken> save(RefreshToken refreshToken);

    // Read
    Optional<RefreshToken> findById(Long id);
    Optional<RefreshToken> findByMemberId(Long memberId);

    // Delete
    Boolean deleteByMemberId(Long memberId);

    // Util
    Boolean existById(Long id);
    Boolean existByMemberId(Long memberId);
}
