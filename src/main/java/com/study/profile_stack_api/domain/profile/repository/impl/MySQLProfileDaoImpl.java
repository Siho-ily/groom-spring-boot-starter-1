package com.study.profile_stack_api.domain.profile.repository.impl;

import com.study.profile_stack_api.domain.profile.entity.Position;
import com.study.profile_stack_api.domain.profile.entity.Profile;
import com.study.profile_stack_api.domain.profile.repository.dao.ProfileDao;
import com.study.profile_stack_api.global.common.Page;
import com.study.profile_stack_api.global.exception.domain.profile.ProfileNotFoundException;
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
public class MySQLProfileDaoImpl implements ProfileDao {
    // DB 접근을 도와주는 jdbc 유틸
    private final JdbcTemplate jdbcTemplate;
    public MySQLProfileDaoImpl(JdbcTemplate jdbcTemplate) {this.jdbcTemplate = jdbcTemplate;}

    // === Create ===
    @Override
    public Profile save(Profile profile) {
        String sql = "insert into profile(member_id, name, email, bio, position, career_years, github_url, blog_url, created_at, updated_at) values(?,?,?,?,?,?,?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"ID"});
            ps.setLong(1, profile.getMemberId());
            ps.setString(2,profile.getName());
            ps.setString(3,profile.getEmail());
            ps.setString(4,profile.getBio());
            ps.setString(5,profile.getPosition().name());
            ps.setInt(6,profile.getCareerYears());
            ps.setString(7,profile.getGithubUrl());
            ps.setString(8,profile.getBlogUrl());
            ps.setTimestamp(9, Timestamp.valueOf(profile.getCreatedAt()));
            ps.setTimestamp(10, Timestamp.valueOf(profile.getUpdatedAt()));
            return ps;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if(generatedId != null){
            profile.setId(generatedId.longValue());
        }

        return profile;
    }

    // === Read ===
    @Override
    public Optional<Profile> findById(Long id) {
        String sql = "SELECT * FROM profile WHERE id = ?";
        try {
            Profile profile = jdbcTemplate.queryForObject(sql, profileRowMapper, id);
            return Optional.ofNullable(profile);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Profile> findByMemberId(Long memberId) {
        String sql = "SELECT * FROM profile WHERE member_id = ?";
        try {
            Profile profile = jdbcTemplate.queryForObject(sql, profileRowMapper, memberId);
            return Optional.ofNullable(profile);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Page<Profile> findWithPage(Integer page, Integer size, String name, Position position) {
        boolean paging = page != null && size != null;

        StringBuilder sql = new StringBuilder("""
        select * from profile
        where 1=1
    """);

        List<Object> params = new ArrayList<>();

        if (name != null && !name.isBlank()) {
            sql.append(" and name like ? ");
            params.add("%" + name + "%");
        }

        if (position != null) {
            sql.append(" and position = ? ");
            params.add(position.name());
        }

        long totalElements = count(name, position);

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

        List<Profile> content = jdbcTemplate.query(
                sql.toString(),
                profileRowMapper,
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
    public Profile update(Profile profile) {
        String sql = """
                update profile
                set name = ?, email = ?, bio = ?, position = ?, career_years = ?, github_url = ?, blog_url = ?
                where id = ?
                """;

        int updated = jdbcTemplate.update(sql,
                profile.getName(),
                profile.getEmail(),
                profile.getBio(),
                profile.getPosition().name(),
                profile.getCareerYears(),
                profile.getGithubUrl(),
                profile.getBlogUrl(),
                profile.getId());

        if (updated == 0) {
            throw new ProfileNotFoundException(profile.getId());
        }

        return profile;
    }


    // === Delete ===
    @Override
    public boolean deleteById(Long id) {
        String sql = "delete from profile where id = ?";
        int updated = jdbcTemplate.update(sql, id);
        if (updated == 0) {
            throw new ProfileNotFoundException(id);
        }
        return true;
    }


    // === Utils ===
    @Override
    public long count(String name, Position position) {
        StringBuilder sql = new StringBuilder("""
        select count(*) from profile
        where 1=1
    """);

        List<Object> params = new ArrayList<>();

        if (name != null && !name.isBlank()) {
            sql.append(" and name like ? ");
            params.add("%" + name + "%");
        }

        if (position != null) {
            sql.append(" and position = ? ");
            params.add(position.name());
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

    @Override
    public boolean existsByEmail(String email) {
        String sql = "select count(*) from profile where email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }


    // === RowMapper ===
    private final RowMapper<Profile> profileRowMapper = (row, index) -> Profile.builder()
            .id(row.getLong("id"))
            .memberId(row.getLong("member_id"))
            .name(row.getString("name"))
            .email(row.getString("email"))
            .bio(row.getString("bio"))
            .position(Position.valueOf(row.getString("position")))
            .careerYears(row.getInt("career_years"))
            .githubUrl(row.getString("github_url"))
            .blogUrl(row.getString("blog_url"))
            .createdAt(row.getTimestamp("created_at").toLocalDateTime())
            .updatedAt(row.getTimestamp("updated_at").toLocalDateTime())
            .build();
}
