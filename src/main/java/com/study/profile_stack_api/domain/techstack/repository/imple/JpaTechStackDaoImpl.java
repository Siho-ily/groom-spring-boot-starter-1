package com.study.profile_stack_api.domain.techstack.repository.imple;

import com.study.profile_stack_api.domain.techstack.entity.Proficiency;
import com.study.profile_stack_api.domain.techstack.entity.TechCategory;
import com.study.profile_stack_api.domain.techstack.entity.TechStack;
import com.study.profile_stack_api.domain.techstack.repository.dao.TechStackDao;
import com.study.profile_stack_api.domain.techstack.repository.jpa.TechStackJpaRepository;
import com.study.profile_stack_api.global.common.Page;
import com.study.profile_stack_api.global.exception.domain.techstack.TechStackNotFoundException;
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
public class JpaTechStackDaoImpl implements TechStackDao {
    private final TechStackJpaRepository techStackJpaRepository;

    @Override
    public TechStack create(Long profileId, TechStack techStack) {
        techStack.setProfileId(profileId);
        return techStackJpaRepository.save(techStack);
    }

    @Override
    public Optional<TechStack> findById(Long profileId, Long techStackId) {
        return techStackJpaRepository.findByProfileIdAndId(profileId, techStackId);
    }

    @Override
    public Page<TechStack> findWithPage(Integer page, Integer size, Long profileId, TechCategory category, Proficiency proficiency) {
        Specification<TechStack> spec = buildSpec(profileId, category, proficiency);

        if (page == null || size == null) {
            List<TechStack> all = techStackJpaRepository.findAll(spec);
            return new Page<>(all, 0, all.size(), all.size(), 1, true, true, false, false);
        }

        org.springframework.data.domain.Page<TechStack> result = techStackJpaRepository.findAll(spec, PageRequest.of(page, size));
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
    public TechStack update(Long profileId, Long techStackId, TechStack techStack) {
        return techStackJpaRepository.save(techStack);
    }

    @Override
    public boolean delete(Long profileId, Long techStackId) {
        if (!techStackJpaRepository.existsByProfileIdAndId(profileId, techStackId)) throw new TechStackNotFoundException(techStackId);
        techStackJpaRepository.deleteById(techStackId);
        return true;
    }

    @Override
    public long count(Long profileId, TechCategory category, Proficiency proficiency) {
        return techStackJpaRepository.count(buildSpec(profileId, category, proficiency));
    }

    @Override
    public boolean existsById(Long id) {
        return techStackJpaRepository.existsById(id);
    }

    @Override
    public boolean existsById(Long profileId, Long techStackId) {
        return techStackJpaRepository.existsByProfileIdAndId(profileId, techStackId);
    }

    private Specification<TechStack> buildSpec(Long profileId, TechCategory category, Proficiency proficiency) {
        Specification<TechStack> spec = (root, query, cb) -> cb.equal(root.get("profileId"), profileId);
        if (category != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category"), category));
        }
        if (proficiency != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("proficiency"), proficiency));
        }
        return spec;
    }
}