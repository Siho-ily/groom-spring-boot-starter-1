package com.study.profile_stack_api.domain.auth.service;

import com.study.profile_stack_api.domain.auth.dto.request.LoginRequest;
import com.study.profile_stack_api.domain.auth.dto.request.SignupRequest;
import com.study.profile_stack_api.domain.auth.dto.request.TokenRefreshRequest;
import com.study.profile_stack_api.domain.auth.dto.response.LoginResponse;
import com.study.profile_stack_api.domain.auth.dto.response.LogoutResponse;
import com.study.profile_stack_api.domain.auth.dto.response.SignupResponse;
import com.study.profile_stack_api.domain.auth.dto.response.TokenRefreshResponse;
import com.study.profile_stack_api.domain.auth.entity.Member;
import com.study.profile_stack_api.domain.auth.entity.RefreshToken;
import com.study.profile_stack_api.domain.auth.mapper.MemberMapper;
import com.study.profile_stack_api.domain.auth.mapper.RefreshTokenMapper;
import com.study.profile_stack_api.domain.auth.repository.dao.MemberDao;
import com.study.profile_stack_api.domain.auth.repository.dao.RefreshTokenDao;
import com.study.profile_stack_api.global.exception.domain.auth.AuthException;
import com.study.profile_stack_api.global.exception.domain.auth.DuplicateMemberUsernameException;
import com.study.profile_stack_api.global.exception.domain.auth.RefreshTokenNotFoundException;
import com.study.profile_stack_api.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberDao memberDao;
    private final RefreshTokenDao refreshTokenDao;
    private final PasswordEncoder passwordEncoder;
    private final MemberMapper memberMapper;
    private final RefreshTokenMapper refreshTokenMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

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
        Member newMember = memberMapper.toMemberEntity(request, encodedPassword);
        Member savedMember = memberDao.save(newMember);

        log.info("User registered successfully: {}", savedMember.getUsername());

        return memberMapper.toSignupResponse(savedMember);
    }

    /**
     * User login
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        try {
            log.info("Login attempt for user: {}", request.getUsername());

            // 1. 미인증 Authentication 토큰 생성 → Spring Security에 인증 요청
            // Client가 입력한 username과 password를 사용하여
            // 인증(입력 값이 DB에 저장되어 있는 값과 일치하는지를 확인하는 역할)을 수행한다.
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // 2. 인증 성공 → DB에서 사용자 정보 조회
            Member member = memberDao.findByUsername(request.getUsername())
                        .orElseThrow(AuthException::new);

            // 3. 권한 정보 추출
            String roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));

            // 4. Access Token + Refresh Token 생성
            String accessToken = jwtTokenProvider.generateAccessToken(member.getId(), member.getUsername(), roles);
            String refreshToken = jwtTokenProvider.generateRefreshToken(member.getUsername());

            // 5. Refresh Token을 DB에 저장
            LocalDateTime expiredDate = jwtTokenProvider.getExpirationFromToken(refreshToken);
            LocalDateTime createdDate = jwtTokenProvider.getCreatedAtFromToken(refreshToken);
            RefreshToken token = refreshTokenMapper.toEntity(member, refreshToken, expiredDate, createdDate);
            refreshTokenDao.save(token).orElseThrow(() -> new RuntimeException("Failed to save refresh token"));

            log.info("Login successful for user: {}", member.getUsername());

            return memberMapper.toLoginResponse(accessToken, refreshToken);

        } catch (AuthenticationException e) {
            log.error("Login failed for user: {}", request.getUsername(), e);
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    /**
     * User refresh
     */
    @Transactional
    public TokenRefreshResponse refresh(TokenRefreshRequest request) {
        String token = request.getRefreshToken();

        // 1. 서명·만료 검증
        jwtTokenProvider.validateToken(token);

        // 2. 리프레시 토큰 타입 확인
        if (!jwtTokenProvider.isRefreshToken(token)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        // 3. username으로 member 조회
        String username = jwtTokenProvider.getUsernameFromToken(token);
        Member member = memberDao.findByUsername(username).orElseThrow(AuthException::new);

        // 4. DB에 저장된 토큰과 일치 여부 확인
        RefreshToken storedToken = refreshTokenDao.findByMemberId(member.getId())
                .orElseThrow(RefreshTokenNotFoundException::new);
        if (!storedToken.getToken().equals(token)) {
            throw new BadCredentialsException("Refresh token mismatch");
        }

        // 5. login과 동일한 포맷으로 role 구성 ("ROLE_" 접두사)
        String role = "ROLE_" + member.getRole().name();

        // 6. 새 Access Token 발급
        String newAccessToken = jwtTokenProvider.generateAccessToken(member.getId(), username, role);

        log.info("Token refreshed for user: {}", username);

        return refreshTokenMapper.toResponse(newAccessToken);
    }

    /**
     * User logout
     */
    @Transactional
    public LogoutResponse logout(String username) {
        Member member = memberDao.findByUsername(username)
                .orElseThrow(AuthException::new);

        refreshTokenDao.deleteByMemberId(member.getId());

        return new LogoutResponse();
    }
}
