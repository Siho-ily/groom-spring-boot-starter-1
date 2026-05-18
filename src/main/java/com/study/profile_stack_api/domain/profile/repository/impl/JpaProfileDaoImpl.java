package com.study.profile_stack_api.domain.profile.repository.impl;

import com.study.profile_stack_api.domain.profile.entity.Position;
import com.study.profile_stack_api.domain.profile.entity.Profile;
import com.study.profile_stack_api.domain.profile.repository.dao.ProfileDao;
import com.study.profile_stack_api.domain.profile.repository.jpa.ProfileJpaRepository;
import com.study.profile_stack_api.global.common.Page;
import com.study.profile_stack_api.global.exception.domain.profile.ProfileNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class JpaProfileDaoImpl implements ProfileDao {
    private final ProfileJpaRepository profileJpaRepository;

    @Override
    public Profile save(Profile profile) {
        return profileJpaRepository.save(profile);
    }

    @Override
    public Optional<Profile> findById(Long id) {
        return profileJpaRepository.findById(id);
    }

    @Override
    public Optional<Profile> findByMemberId(Long memberId) {
        return profileJpaRepository.findByMemberId(memberId);
    }

    @Override
    public Page<Profile> findWithPage(Integer page, Integer size, String name, Position position) {
        Specification<Profile> spec = buildSpec(name, position);

        if (page == null || size == null) {
            List<Profile> all = profileJpaRepository.findAll(spec);
            return new Page<>(all, 0, all.size(), all.size(), 1, true, true, false, false);
        }

        org.springframework.data.domain.Page<Profile> result = profileJpaRepository.findAll(spec, PageRequest.of(page, size));
        return new Page<>(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isFirst(),
                result.isLast(),
                result.hasPrevious(),
                result.hasNext()
        );
    }

    @Override
    public Profile update(Profile profile) {
        return profileJpaRepository.save(profile);
    }

    @Override
    public boolean deleteById(Long id) {
        if (!profileJpaRepository.existsById(id)) throw new ProfileNotFoundException(id);
        profileJpaRepository.deleteById(id);
        return true;
    }

    @Override
    public long count(String name, Position position) {
        return profileJpaRepository.count(buildSpec(name, position));
    }

    @Override
    public boolean existsById(Long id) {
        return profileJpaRepository.existsById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return profileJpaRepository.existsByEmail(email);
    }

    private Specification<Profile> buildSpec(String name, Position position) {
        Specification<Profile> spec = (root, query, cb) -> cb.conjunction();
        if (name != null && !name.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("name"), "%" + name + "%"));
        }
        if (position != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("position"), position));
        }
        return spec;
    }
}