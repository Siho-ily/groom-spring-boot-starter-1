package com.study.profile_stack_api.domain.techstack.repository.imple;

import com.study.profile_stack_api.domain.techstack.entity.Proficiency;
import com.study.profile_stack_api.domain.techstack.entity.TechCategory;
import com.study.profile_stack_api.domain.techstack.entity.TechStack;
import com.study.profile_stack_api.domain.techstack.repository.dao.TechStackDao;
import com.study.profile_stack_api.global.common.Page;
import com.study.profile_stack_api.global.exception.TechStackNotFoundException;
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
import java.util.ArrayList;
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
    public Page<TechStack> findWithPage(Integer page, Integer size, Long profileId, String category, String proficiency) {
        boolean paging = page != null && size != null;

        StringBuilder sql = new StringBuilder("""
                select * from tech_stack
                where profile_id = ?
                """);

        List<Object> params = new ArrayList<>();
        params.add(profileId);

        if (category != null && !category.isBlank()) {
            sql.append(" and category like ? ");
            params.add("%" + category + "%");
        }

        if (proficiency != null && !proficiency.isBlank()) {
            sql.append(" and proficiency like ? ");
            params.add("%" + proficiency + "%");
        }

        long totalElements = count(profileId, category, proficiency);

        int totalPages = 1;
        int currentPage = 0;
        int currentSize = (size == null ? (int) totalElements : size);

        if (paging) {
            int offset = page * size;
            sql.append(" limit ? offset ? ");
            params.add(size);
            params.add(offset);

            totalPages = (int) Math.ceil((double) totalElements / size);
            currentPage = page;
        }

        List<TechStack> content = jdbcTemplate.query(
                sql.toString(),
                techStackRowMapper,
                params.toArray()
        );

        boolean first = !paging || currentPage == 0;
        boolean last = !paging || currentPage >= totalPages - 1;
        boolean hasPrevious = paging && currentPage > 0;
        boolean hasNext = paging && currentPage + 1 < totalPages;

        return new Page<>(
                content,
                currentPage,
                currentSize,
                totalElements,
                totalPages,
                first,
                last,
                hasPrevious,
                hasNext
        );
    }

    // === Update ===
    @Override
    public TechStack update(Long profileId, Long techStackId, TechStack techStack) {
        String sql = """
                update tech_stack
                set name = ?, category = ?, proficiency = ?, years_of_exp = ?
                where id=?
                """;

        int updated = jdbcTemplate.update(sql,
                techStack.getName(),
                techStack.getCategory().name(),
                techStack.getProficiency().name(),
                techStack.getYearsOfExp(),
                techStackId
        );

        if(updated == 0){
            throw new TechStackNotFoundException(techStackId);
        }

        return techStack;
    }

    // === Delete ===
    @Override
    public boolean delete(Long techStackId) {
        if (!existsById(techStackId)) throw new TechStackNotFoundException(techStackId);

        String sql = "delete from tech_stack where id=?";
        int updated = jdbcTemplate.update(sql, techStackId);

        if(updated == 0){
            throw new TechStackNotFoundException(techStackId);
        }

        return true;
    }

    // === Utils ===
    @Override
    public long count(Long profileId, String category, String proficiency) {
        StringBuilder sql = new StringBuilder("""
            select count(*) from tech_stack
            where profile_id = ?
        """);

        List<Object> params = new ArrayList<>();
        params.add(profileId);

        if (category != null && !category.isBlank()) {
            sql.append(" and category like ? ");
            params.add("%" + category + "%");
        }

        if (proficiency != null && !proficiency.isBlank()) {
            sql.append(" and proficiency like ? ");
            params.add("%" + proficiency + "%");
        }

        return jdbcTemplate.queryForObject(
                sql.toString(),
                Long.class,
                params.toArray()
        );
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "select count(*) from profile where id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
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
