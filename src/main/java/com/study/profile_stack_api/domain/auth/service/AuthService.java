package com.study.profile_stack_api.domain.auth.service;

import com.study.profile_stack_api.domain.auth.dto.request.SignupRequest;
import com.study.profile_stack_api.domain.auth.dto.request.SignupResponse;
import com.study.profile_stack_api.domain.auth.repository.dao.MemberDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
//
//    private final MemberDao memberDao;
//    private final PasswordEncoder passwordEncoder;
//
//    /**
//     * User signup
//     */
//    @Transactional
//    public SignupResponse signup(SignupRequest request) {
//        log.info("Signup attempt for username: {}, email: {}", request.getUsername(), request.getEmail());
//
//        // 1. 아이디 중복 검사
//        if (memberDao.existsByUsername(request.getUsername())) {
//            throw new DuplicateResourceException("Username already exists: " + request.getUsername());
//        }
//
//        // 2. 이메일 중복 검사
//        if (memberDao.existsByEmail(request.getEmail())) {
//            throw new DuplicateResourceException("Email already exists: " + request.getEmail());
//        }
//
//        // 3. 사용자 엔티티 생성 및 저장
//        // Password를 평문으로 저장을 하면 안된다.
//        // BCryptPasswordEncoder를 사용하여 암호화된 비밀번호를 저장한다.
//        User newUser = User.builder()
//                .username(request.getUsername())
//                .email(request.getEmail())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .role(UserRole.USER)
//                .enabled(true)
//                .build();
//
//        User savedUser = memberDao.save(newUser);
//
//        log.info("User registered successfully: {}", savedUser.getUsername());
//
//        return SignupResponse.of(
//                savedUser.getId(),
//                savedUser.getUsername(),
//                savedUser.getEmail()
//        );
//    }
}
