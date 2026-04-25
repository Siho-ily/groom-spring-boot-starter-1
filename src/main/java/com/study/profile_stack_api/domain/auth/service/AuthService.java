package com.study.profile_stack_api.domain.auth.service;

import com.study.profile_stack_api.domain.auth.dto.request.SignupRequest;
import com.study.profile_stack_api.domain.auth.dto.response.SignupResponse;
import com.study.profile_stack_api.domain.auth.entity.Member;
import com.study.profile_stack_api.domain.auth.mapper.MemberMapper;
import com.study.profile_stack_api.domain.auth.repository.dao.MemberDao;
import com.study.profile_stack_api.global.exception.domain.auth.DuplicateMemberUsernameException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberDao memberDao;
    private final PasswordEncoder passwordEncoder;
    private final MemberMapper memberMapper;

    /**
     * User signup
     */
    @Transactional
    public SignupResponse signup(SignupRequest request) {
        log.info("Signup attempt for username: {}", request.getUsername());

        if (memberDao.existByUsername(request.getUsername())) {
            throw new DuplicateMemberUsernameException(request.getUsername());
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        Member newMember = memberMapper.toEntity(request, encodedPassword);
        Member savedMember = memberDao.save(newMember);

        log.info("User registered successfully: {}", savedMember.getUsername());

        return memberMapper.toResponse(savedMember);
    }
}
