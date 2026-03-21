package com.study.profile_stack_api.domain.profile.repository.impl;

import com.study.profile_stack_api.ProfileStackApiApplication;
import com.study.profile_stack_api.domain.profile.entity.Position;
import com.study.profile_stack_api.domain.profile.entity.Profile;
import com.study.profile_stack_api.domain.profile.repository.dao.ProfileDao;
import com.study.profile_stack_api.global.common.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ProfileStackApiApplication.class)
class MySQLProfileDaoImplTest {

    @Autowired
    private ProfileDao dao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("delete from tech_stack");
        jdbcTemplate.update("delete from profile");
        jdbcTemplate.update("delete from refresh_token");
        jdbcTemplate.update("delete from member");
    }

    @Test
    void save() {
        Long memberId = createMember("save-user");
        Profile profile = createProfile(memberId, "세이브", "save.profile@example.com", Position.BACKEND);

        Profile saved = dao.save(profile);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getMemberId()).isEqualTo(memberId);
        assertThat(saved.getEmail()).isEqualTo("save.profile@example.com");

        Profile found = dao.findById(saved.getId()).orElseThrow();
        assertThat(found.getName()).isEqualTo("세이브");
        assertThat(found.getPosition()).isEqualTo(Position.BACKEND);
    }

    @Test
    void findById() {
        Long memberId = createMember("find-user");
        Profile saved = dao.save(createProfile(memberId, "조회대상", "find.profile@example.com", Position.FRONTEND));

        Profile found = dao.findById(saved.getId()).orElseThrow();

        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getMemberId()).isEqualTo(memberId);
        assertThat(found.getName()).isEqualTo("조회대상");
        assertThat(found.getEmail()).isEqualTo("find.profile@example.com");
        assertThat(found.getPosition()).isEqualTo(Position.FRONTEND);
    }

    @Test
    void findWithPage() {
        dao.save(createProfile(createMember("backend-a"), "백엔드A", "profile-a@example.com", Position.BACKEND));
        dao.save(createProfile(createMember("frontend-b"), "프론트B", "profile-b@example.com", Position.FRONTEND));
        dao.save(createProfile(createMember("backend-c"), "백엔드C", "profile-c@example.com", Position.BACKEND));

        Page<Profile> page = dao.findWithPage(0, 2, "백엔드", Position.BACKEND);

        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.isLast()).isTrue();
        assertThat(page.isHasPrevious()).isFalse();
        assertThat(page.isHasNext()).isFalse();
        assertThat(page.getContent())
                .extracting(Profile::getName)
                .containsExactlyInAnyOrder("백엔드A", "백엔드C");
    }

    @Test
    void update() {
        Long memberId = createMember("update-user");
        Profile saved = dao.save(createProfile(memberId, "수정전", "before.update@example.com", Position.BACKEND));

        saved.update(
                "수정후",
                "after.update@example.com",
                "수정된 소개",
                Position.FULLSTACK,
                7,
                "https://github.com/updated",
                "https://updated.blog"
        );

        Profile updated = dao.update(saved);
        Profile found = dao.findById(updated.getId()).orElseThrow();

        assertThat(found.getName()).isEqualTo("수정후");
        assertThat(found.getEmail()).isEqualTo("after.update@example.com");
        assertThat(found.getBio()).isEqualTo("수정된 소개");
        assertThat(found.getPosition()).isEqualTo(Position.FULLSTACK);
        assertThat(found.getCareerYears()).isEqualTo(7);
        assertThat(found.getGithubUrl()).isEqualTo("https://github.com/updated");
        assertThat(found.getBlogUrl()).isEqualTo("https://updated.blog");
    }

    @Test
    void deleteById() {
        Long memberId = createMember("delete-user");
        Profile saved = dao.save(createProfile(memberId, "삭제대상", "delete.profile@example.com", Position.DEVOPS));

        boolean deleted = dao.deleteById(saved.getId());

        assertThat(deleted).isTrue();
        assertThat(dao.findById(saved.getId())).isEmpty();
        assertThat(dao.existsById(saved.getId())).isFalse();
    }

    @Test
    void count() {
        dao.save(createProfile(createMember("count-a"), "카운트A", "count-profile-a@example.com", Position.BACKEND));
        dao.save(createProfile(createMember("count-b"), "카운트B", "count-profile-b@example.com", Position.BACKEND));
        dao.save(createProfile(createMember("count-c"), "카운트C", "count-profile-c@example.com", Position.FRONTEND));

        long backendCount = dao.count(null, Position.BACKEND);
        long nameFilteredCount = dao.count("카운트", null);

        assertThat(backendCount).isEqualTo(2);
        assertThat(nameFilteredCount).isEqualTo(3);
    }

    @Test
    void existsById() {
        Long memberId = createMember("exists-id-user");
        Profile saved = dao.save(createProfile(memberId, "아이디체크", "exists-id-profile@example.com", Position.AI));

        assertThat(dao.existsById(saved.getId())).isTrue();
        assertThat(dao.existsById(99999L)).isFalse();
    }

    @Test
    void existsByEmail() {
        Long memberId = createMember("exists-email-user");
        dao.save(createProfile(memberId, "이메일체크", "exists-email-profile@example.com", Position.MOBILE));

        assertThat(dao.existsByEmail("exists-email-profile@example.com")).isTrue();
        assertThat(dao.existsByEmail("missing@example.com")).isFalse();
    }

    // 멤버 추가, 생성된 id 반환
    private Long createMember(String username) {
        String sql = "insert into member(username, password, role) values (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"ID"});
            ps.setString(1, username);
            ps.setString(2, "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");
            ps.setString(3, "USER");
            return ps;
        }, keyHolder);

        return (Long) keyHolder.getKey();
    }

    // 프로필 생성
    private Profile createProfile(Long memberId, String name, String email, Position position) {
        LocalDateTime now = LocalDateTime.now();

        return Profile.builder()
                .memberId(memberId)
                .name(name)
                .email(email)
                .bio(name + " 소개")
                .position(position)
                .careerYears(3)
                .githubUrl("https://github.com/" + name)
                .blogUrl("https://blog.example.com/" + name)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
