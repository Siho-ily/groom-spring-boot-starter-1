package com.study.profile_stack_api.domain.profile.repository.impl;

import com.study.profile_stack_api.domain.profile.dto.response.ProfileResponse;
import com.study.profile_stack_api.domain.profile.entity.Position;
import com.study.profile_stack_api.domain.profile.entity.Profile;
import com.study.profile_stack_api.domain.profile.repository.dao.ProfileDao;
import com.study.profile_stack_api.global.common.Page;
import com.study.profile_stack_api.global.error.ErrorCode;
import com.study.profile_stack_api.global.exception.ProfileNotFoundException;
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
public class MySQLProfileDaoImpl implements ProfileDao {
    // DB 접근을 도와주는 jdbc 유틸
    private final JdbcTemplate jdbcTemplate;
    public MySQLProfileDaoImpl(JdbcTemplate jdbcTemplate) {this.jdbcTemplate = jdbcTemplate;}

    // === Create ===
    @Override
    public Profile save(Profile profile) {
        String sql = "insert into profile(name, email, bio, position, career_years, github_url, blog_url, created_at, updated_at) values(?,?,?,?,?,?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"ID"});
            ps.setString(1,profile.getName());
            ps.setString(2,profile.getEmail());
            ps.setString(3,profile.getBio());
            ps.setString(4,profile.getPosition().name());
            ps.setInt(5,profile.getCareerYears());
            ps.setString(6,profile.getGithubUrl());
            ps.setString(7,profile.getBlogUrl());
            ps.setTimestamp(8, Timestamp.valueOf(profile.getCreatedAt()));
            ps.setTimestamp(9, Timestamp.valueOf(profile.getUpdatedAt()));
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

    public Page<ProfileResponse> findWithPage(int page, int size) {
        long totalElements = count();

        int totalPages = (int) Math.ceil((double) totalElements / size);
        int offset = page * size;

        String sql = "select * from profile limit ? offset ?";
        List<Profile> content = jdbcTemplate.query(sql, profileRowMapper, size, offset);

        // Entity -> DTO
        List<ProfileResponse> profileResponses = content
                .stream()
                .map(ProfileResponse::from)
                .toList();

        boolean first = (page == 0);
        boolean last = (page >= totalPages - 1);     // totalPages=0이면 last 처리 주의
        boolean hasPrevious = (page > 0 && page <= totalPages);
        boolean hasNext = (page + 1 < totalPages);

        return new Page<>(profileResponses, page, size, totalElements, totalPages, first, last, hasPrevious, hasNext);
    }


    @Override
    public List<Profile> findByPosition(String position) {
        return List.of();
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
    public long count() {
        String sql = "select count(*) from profile";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count == null ? 0 : count;
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
    private final RowMapper<Profile> profileRowMapper = (row, index) -> {
        Profile profile = new Profile();
        profile.setId(row.getLong("id"));
        profile.setName(row.getString("name"));
        profile.setEmail(row.getString("email"));
        profile.setBio(row.getString("bio"));
        profile.setPosition(Position.valueOf(row.getString("position")));
        profile.setCareerYears(row.getInt("career_years"));
        profile.setGithubUrl(row.getString("github_url"));
        profile.setBlogUrl(row.getString("blog_url"));
        profile.setCreatedAt(row.getTimestamp("created_at").toLocalDateTime());
        profile.setUpdatedAt(row.getTimestamp("updated_at").toLocalDateTime());
        return profile;
    };
}
