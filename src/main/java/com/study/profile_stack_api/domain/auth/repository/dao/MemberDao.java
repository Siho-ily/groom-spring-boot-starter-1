package com.study.profile_stack_api.domain.auth.repository.dao;

import com.study.profile_stack_api.domain.auth.entity.Member;

import java.util.Optional;

public interface MemberDao {

    // Create
    Member save(Member member);

    // Read
    Optional<Member> findById(Long id);
    Optional<Member> findByName(String name);

    // Update
    Optional<Member> update(Member member);

    // Delete
    Boolean delete(Long id);

    // Util
    Boolean existById(Long id);
    Boolean existByName(String name);
}
