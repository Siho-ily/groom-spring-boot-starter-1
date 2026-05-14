package com.study.profile_stack_api.domain.auth.repository.impl;

import com.study.profile_stack_api.domain.auth.entity.Member;
import com.study.profile_stack_api.domain.auth.repository.dao.MemberDao;
import com.study.profile_stack_api.domain.auth.repository.jpa.MemberJpaRepository;
import com.study.profile_stack_api.global.exception.domain.auth.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class JpaMemberDaoImpl implements MemberDao {
    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Member save(Member member) {
        return memberJpaRepository.save(member);
    }

    @Override
    public Optional<Member> findById(Long id) {
        return memberJpaRepository.findById(id);
    }

    @Override
    public Optional<Member> findByUsername(String username) {
        return memberJpaRepository.findByUsername(username);
    }

    @Override
    public Optional<Member> update(Member member) {
        return Optional.of(memberJpaRepository.save(member));
    }

    @Override
    public Boolean delete(Long id) {
        if (!memberJpaRepository.existsById(id)) throw new MemberNotFoundException(id);
        memberJpaRepository.deleteById(id);
        return true;
    }

    @Override
    public Boolean existById(Long id) {
        return memberJpaRepository.existsById(id);
    }

    @Override
    public Boolean existByUsername(String username) {
        return memberJpaRepository.existsByUsername(username);
    }
}