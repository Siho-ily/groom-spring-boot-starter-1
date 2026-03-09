package com.study.profile_stack_api.domain.techstack.repository.imple;


import com.study.profile_stack_api.domain.profile.dto.response.ProfileResponse;
import com.study.profile_stack_api.domain.profile.entity.Profile;
import com.study.profile_stack_api.domain.techstack.dto.response.TechStackResponse;
import com.study.profile_stack_api.domain.techstack.entity.Proficiency;
import com.study.profile_stack_api.domain.techstack.entity.TechCategory;
import com.study.profile_stack_api.domain.techstack.entity.TechStack;
import com.study.profile_stack_api.domain.techstack.repository.dao.TechStackDao;
import com.study.profile_stack_api.global.common.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class MySQLTechStackImpl implements TechStackDao {
    // DB 접근을 도와주는 jdbc 유틸
    private final JdbcTemplate jdbcTemplate;

    // === Create ===
    @Override
    public TechStack create(Long profileId, TechStack techStack) {
        String sql = "insert into tech_stack (profile_id, name, category, proficiency, years_of_exp, created_at, updated_at) values(?,?,?,?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"ID"});
            ps.setLong(1, profileId);
            ps.setString(2, techStack.getName());
            ps.setString(3, techStack.getCategory().name());
            ps.setString(4, techStack.getProficiency().name());
            ps.setInt(5, techStack.getYearsOfExp());
            ps.setTimestamp(6, Timestamp.valueOf(techStack.getCreatedAt()));
            ps.setTimestamp(7, Timestamp.valueOf(techStack.getUpdatedAt()));

            return ps;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if(generatedId != null){
            techStack.setId(generatedId.longValue());
        }

        return techStack;
    }

    // === Read ===
    @Override
    public Optional<TechStack> findById(Long profileId, Long techStackId) {
        String sql = "select * from tech_stack where profile_id=? and id=?";
        try {
            TechStack techStack = jdbcTemplate.queryForObject(sql, techStackRowMapper, profileId, techStackId);
            return Optional.ofNullable(techStack);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Page<TechStackResponse> findWithPage(Long profileId, int page, int size) {
        long totalElements = countByProfileId(profileId);

        int totalPages = (int) Math.ceil((double) totalElements / size);
        int offset = page * size;

        String sql = "select * from tech_stack where profile_id = ? limit ? offset ?";
        List<TechStack> techStacks = jdbcTemplate.query(sql, techStackRowMapper, profileId, size, offset);

        // Entity -> DTO
        List<TechStackResponse> techStackResponses = techStacks
                .stream()
                .map(TechStackResponse::from)
                .toList();

        boolean first = (page == 0);
        boolean last = (page >= totalPages - 1);     // totalPages=0이면 last 처리 주의
        boolean hasPrevious = (page > 0 && page <= totalPages);
        boolean hasNext = (page + 1 < totalPages);

        return new Page<>(techStackResponses, page, size, totalElements, totalPages, first, last, hasPrevious, hasNext);
    }

    @Override
    public Page<TechStackResponse> findByCategory(int page, int limit, String category) {
        return null;
    }

    // === Update ===
    @Override
    public TechStack update(Long profileId, Long techStackId, TechStack techStack) {
        return null;
    }

    // === Delete ===
    @Override
    public boolean delete(Long profileId, Long techStackId) {
        return false;
    }

    // === Utils ===
    @Override
    public long countByProfileId(Long profileId) {
        String sql = "select count(*) from tech_stack where profile_id = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, profileId);
        return count == null ? 0 : count;
    }

    @Override
    public boolean existsById(Long profileId, Long techStackId) {
        return false;
    }


    // === RowMapper ===
    private final RowMapper<TechStack> techStackRowMapper = (row, index) -> {
        TechStack techStack = new TechStack();
        techStack.setId(row.getLong("id"));
        techStack.setProfileId(row.getLong("profile_id"));
        techStack.setName(row.getString("name"));
        techStack.setCategory(TechCategory.valueOf(row.getString("category")));
        techStack.setProficiency(Proficiency.valueOf(row.getString("proficiency")));
        techStack.setYearsOfExp(row.getInt("years_of_exp"));
        techStack.setCreatedAt(row.getTimestamp("created_at").toLocalDateTime());
        techStack.setUpdatedAt(row.getTimestamp("updated_at").toLocalDateTime());
        return techStack;
    };
}
