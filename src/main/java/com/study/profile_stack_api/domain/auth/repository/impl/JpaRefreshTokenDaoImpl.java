package com.study.profile_stack_api.domain.auth.repository.impl;

import com.study.profile_stack_api.domain.auth.entity.RefreshToken;
import com.study.profile_stack_api.domain.auth.repository.dao.RefreshTokenDao;
import com.study.profile_stack_api.domain.auth.repository.jpa.RefreshTokenJpaRepository;
import com.study.profile_stack_api.global.exception.domain.auth.RefreshTokenNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class JpaRefreshTokenDaoImpl implements RefreshTokenDao {
    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Override
    public Optional<RefreshToken> save(RefreshToken refreshToken) {
        return Optional.of(refreshTokenJpaRepository.save(refreshToken));
    }

    @Override
    public Optional<RefreshToken> findById(Long id) {
        return refreshTokenJpaRepository.findById(id);
    }

    @Override
    public Optional<RefreshToken> findByMemberId(Long memberId) {
        return refreshTokenJpaRepository.findByMemberId(memberId);
    }

    @Override
    @Transactional
    public Boolean deleteByMemberId(Long memberId) {
        if (!refreshTokenJpaRepository.existsByMemberId(memberId)) throw new RefreshTokenNotFoundException();
        refreshTokenJpaRepository.deleteByMemberId(memberId);
        return true;
    }

    @Override
    public Boolean existById(Long id) {
        return refreshTokenJpaRepository.existsById(id);
    }

    @Override
    public Boolean existByMemberId(Long memberId) {
        return refreshTokenJpaRepository.existsByMemberId(memberId);
    }
}