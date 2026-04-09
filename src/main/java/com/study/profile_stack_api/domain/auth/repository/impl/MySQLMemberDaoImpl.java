package com.study.profile_stack_api.domain.auth.repository.impl;

import com.study.profile_stack_api.domain.auth.entity.Member;
import com.study.profile_stack_api.domain.auth.entity.Role;
import com.study.profile_stack_api.domain.auth.repository.dao.MemberDao;
import com.study.profile_stack_api.global.exception.domain.auth.MemberNotFoundException;
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
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@Primary
@AllArgsConstructor
public class MySQLMemberDaoImpl implements MemberDao {
    private JdbcTemplate jdbcTemplate;


    @Override
    public Member save(Member member) {
        String sql = "insert into member (username, password, role, created_at) values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update((connection) -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"ID"});
            ps.setString(1, member.getName());
            ps.setString(2, member.getPassword());
            ps.setString(3, member.getRole().name());
            ps.setTimestamp(4, Timestamp.valueOf(member.getCreatedAt()));
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        Member new_member = null;
        if (key != null) {
            new_member = Member.builder()
                    .id(key.longValue())
                    .name(member.getName())
                    .password(member.getPassword())
                    .role(member.getRole())
                    .createdAt(member.getCreatedAt())
                    .build();
        }

        return new_member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        String sql = "SELECT * FROM member WHERE id = ?";
        try {
            Member member = jdbcTemplate.queryForObject(sql, rowMapper, id);
            return Optional.ofNullable(member);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Member> update(Member member) {
        String sql = """
                update member
                set name = ?, password = ?, role = ?
                where id = ?
                """;

        int updated = jdbcTemplate.update(sql,
                member.getName(),
                member.getPassword(),
                member.getRole().name(),
                member.getId());
        if (updated == 0) {
            throw new MemberNotFoundException(member.getId());
        }

        return Optional.of(member);
    }

    @Override
    public Boolean delete(Long id) {
        String sql = "delete from member where id = ?";
        int updated = jdbcTemplate.update(sql, id);
        if (updated == 0) {
            throw new MemberNotFoundException(id);
        }
        return true;
    }

    @Override
    public Boolean existById(Long id) {
        String sql = "select count(*) from member where id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

//    @Override
//    public Boolean existByUsername(String username) {
//        return null;
//    }
//
//    @Override
//    public Optional<Member> findByUsername(String name) {
//        return Optional.empty();
//    }


    // === RowMapper ===
    private final RowMapper<Member> rowMapper = (row, index) -> Member.builder()
            .id(row.getLong("id"))
            .name(row.getString("name"))
            .password(row.getString("password"))
            .role(Role.valueOf(row.getString("role")))
            .createdAt(row.getTimestamp("created_at").toLocalDateTime())
            .build();
}
