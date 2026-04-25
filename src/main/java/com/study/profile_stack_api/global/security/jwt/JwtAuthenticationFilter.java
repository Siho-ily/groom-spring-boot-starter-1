package com.study.profile_stack_api.global.security.jwt;

import com.study.profile_stack_api.global.exception.domain.auth.ExpiredTokenException;
import com.study.profile_stack_api.global.exception.domain.auth.InvalidTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
// 요청마다 한 번만 실행되며, JWT를 읽어 SecurityContext에 인증 정보를 넣는 필터
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final Environment environment;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            // Authorization 헤더에서 Bearer 토큰을 추출한다.
            String token = extractTokenFromRequest(request);

            // 토큰이 있으면 검증 후 인증 객체를 SecurityContext에 저장한다.
            if (token != null) {
                authenticateUser(token, request);
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            // 인증 실패 사유를 요청에 남겨두고, 이후 예외 처리 진입점에서 응답을 결정할 수 있게 한다.
            log.error("JWT authentication failed: {}", e.getMessage());
            request.setAttribute("exception", e);
        } catch (Exception e) {
            // JWT 처리 중 예상하지 못한 예외도 동일하게 다음 처리 단계로 넘긴다.
            log.error("Unexpected error during JWT authentication", e);
            request.setAttribute("exception", e);
        }

        // 인증 성공/실패와 관계없이 다음 필터 또는 컨트롤러로 요청을 전달한다.
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // H2 콘솔은 개발 프로파일에서만 열어두므로, JWT 필터 예외도 동일한 조건으로 맞춘다.
        boolean isDevProfile = environment.acceptsProfiles(Profiles.of("dev"));

        // 로그인/토큰 발급 관련 경로는 JWT 검사를 생략한다.
        return path.startsWith("/api/v1/auth/")
                || (isDevProfile && ("/h2-console".equals(path) || path.startsWith("/h2-console/")))
                || (isDevProfile && ("/swagger-ui/index.html".equals(path) || path.startsWith("/swagger-ui/")))
                || path.startsWith("/v1/api-docs");
    }

    /**
     * Authorization 헤더에서 "Bearer {token}" 형식의 JWT를 꺼낸다.
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        return null;
    }

    /**
     * JWT가 유효하면 사용자 정보와 권한을 꺼내 인증 객체를 만든다.
     */
    private void authenticateUser(String token, HttpServletRequest request) {
        // 토큰을 한 번만 파싱해 username과 roles를 함께 추출한다.
        JwtTokenProvider.TokenClaims claims = jwtTokenProvider.parseAccessToken(token);
        String username = claims.username();

        // 토큰에 담긴 권한 문자열을 Security 권한 목록으로 변환한다.
        List<SimpleGrantedAuthority> authorities = parseAuthorities(claims.roles());

        // DB 조회 없이, 토큰 안의 정보만으로 UserDetails를 구성한다.
        UserDetails userDetails = User.builder()
                .username(username)
                .password("") // JWT 인증에서는 비밀번호 검증을 다시 하지 않는다.
                .authorities(authorities)
                .build();

        // Spring Security가 사용할 인증 객체를 생성한다.
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        // 요청 IP, 세션 정보 같은 부가 정보를 인증 객체에 담는다.
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // 이후 인가 처리에서 사용할 수 있도록 SecurityContext에 저장한다.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.debug("Authenticated user: {}", username);
    }

    /**
     * 쉼표로 구분된 권한 문자열을 GrantedAuthority 목록으로 바꾼다.
     */
    private List<SimpleGrantedAuthority> parseAuthorities(String rolesString) {
        if (rolesString == null || rolesString.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.stream(rolesString.split(","))
                .map(String::trim)
                .filter(role -> !role.isEmpty())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
