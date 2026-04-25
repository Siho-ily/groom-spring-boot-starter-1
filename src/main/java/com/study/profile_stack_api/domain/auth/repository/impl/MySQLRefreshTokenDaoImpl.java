package com.study.profile_stack_api.domain.auth.repository.impl;

import com.study.profile_stack_api.domain.auth.entity.RefreshToken;
import com.study.profile_stack_api.domain.auth.repository.dao.RefreshTokenDao;
import com.study.profile_stack_api.global.exception.domain.auth.RefreshTokenNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Optional;

@Repository
@Primary
@AllArgsConstructor
public class MySQLRefreshTokenDaoImpl implements RefreshTokenDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<RefreshToken> save(RefreshToken token) {
        String sql = "insert into refresh_token (member_id, token, expiry_date, created_at) values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update((connection) -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"ID"});
            ps.setLong(1, token.getMemberId());
            ps.setString(2, token.getToken());
            ps.setTimestamp(3, Timestamp.valueOf(token.getExpiredDate()));
            ps.setTimestamp(4, Timestamp.valueOf(token.getCreatedAt()));
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        RefreshToken new_token = null;
        if (key != null) {
            new_token = RefreshToken.builder()
                    .id(key.longValue())
                    .memberId(token.getMemberId())
                    .token(token.getToken())
                    .expiredDate(token.getExpiredDate())
                    .createdAt(token.getCreatedAt())
                    .build();
        }

        return Optional.ofNullable(new_token);
    }

    @Override
    public Optional<RefreshToken> findById(Long id) {
        String sql = "SELECT * FROM refresh_token WHERE id = ?";
        try {
            RefreshToken token = jdbcTemplate.queryForObject(sql, rowMapper, id);
            return Optional.ofNullable(token);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<RefreshToken> findByMemberId(Long memberId) {
        String sql = "SELECT * FROM refresh_token WHERE member_id = ?";
        try {
            RefreshToken token = jdbcTemplate.queryForObject(sql, rowMapper, memberId);
            return Optional.ofNullable(token);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Boolean deleteByMemberId(Long memberId) {
        String sql = "delete from refresh_token where member_id = ?";
        int updated = jdbcTemplate.update(sql, memberId);
        if (updated == 0) {
            throw new RefreshTokenNotFoundException();
        }
        return true;
    }

    @Override
    public Boolean existById(Long id) {
        String sql = "select count(*) from refresh_token where id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public Boolean existByMemberId(Long memberId) {
        String sql = "select count(*) from refresh_token where memberId = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, memberId);
        return count != null && count > 0;
    }

    // === RowMapper ===
    private final RowMapper<RefreshToken> rowMapper = (row, index) -> RefreshToken.builder()
            .id(row.getLong("id"))
            .memberId(row.getLong("member_id"))
            .token(row.getString("token"))
            .expiredDate(row.getTimestamp("expiry_date").toLocalDateTime())
            .createdAt(row.getTimestamp("created_at").toLocalDateTime())
            .build();
}
