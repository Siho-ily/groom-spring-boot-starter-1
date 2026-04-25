package com.study.profile_stack_api.global.security.jwt;


import com.study.profile_stack_api.global.exception.domain.auth.ExpiredTokenException;
import com.study.profile_stack_api.global.exception.domain.auth.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JWT 생성, 검증, 클레임 추출을 담당하는 컴포넌트다.
 */
@Slf4j
@Component
public class JwtTokenProvider {

    // 서명 및 검증에 사용하는 비밀키
    private final SecretKey secretKey;
    // 액세스 토큰 만료 시간(ms)
    private final long accessTokenValidityInMilliseconds;
    // 리프레시 토큰 만료 시간(ms)
    private final long refreshTokenValidityInMilliseconds;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenValidity,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenValidity) {

        // 설정 파일의 문자열 시크릿을 HMAC 서명 키로 변환한다.
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityInMilliseconds = accessTokenValidity * 1000;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidity * 1000;
    }

    /**
     * 인증 객체에서 사용자명과 권한을 꺼내 액세스 토큰을 만든다.
     */
    public String generateAccessToken(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        String username;

        if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else {
            username = authentication.getName();
        }

        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return generateAccessToken(username, roles);
    }

    /**
     * 사용자명과 권한 문자열로 액세스 토큰을 만든다.
     */
    public String generateAccessToken(String username, String roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .claim("type", "access")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 사용자명으로 리프레시 토큰을 만든다.
     */
    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenValidityInMilliseconds);
        String token = Jwts.builder()
                .subject(username)
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();

        return token;
    }

    /**
     * 유효한 토큰에서 subject(username)를 추출한다.
     */
    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * 유효한 토큰에서 roles 클레임을 추출한다.
     */
    public String getRolesFromToken(String token) {
        return getClaims(token).get("roles", String.class);
    }

    /**
     * 토큰을 한 번만 파싱해 username과 roles를 묶어 반환한다.
     */
    public TokenClaims parseAccessToken(String token) {
        Claims claims = getClaims(token);
        return new TokenClaims(
                claims.getSubject(),
                claims.get("roles", String.class)
        );
    }

    /**
     * 토큰이 서명, 형식, 만료 시간 기준으로 유효한지 검증한다.
     */
    public boolean validateToken(String token) {
        getClaims(token);
        return true;
    }

    /**
     * 유효한 토큰인지 확인한 뒤 리프레시 토큰 여부를 반환한다.
     */
    public boolean isRefreshToken(String token) {
        String type = getClaims(token).get("type", String.class);
        return "refresh".equals(type);
    }

    /**
     * 유효한 토큰의 만료 시각을 반환한다.
     */
    public LocalDateTime getExpirationFromToken(String token) {
        return getClaims(token).getExpiration().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     *
     * 유효한 토큰의 생성 시각을 반환한다.
     */
    public LocalDateTime getCreatedAtFromToken(String token) {
        return getClaims(token).getIssuedAt().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public record TokenClaims(String username, String roles) {}

    /**
     * JWT 파싱과 서명 검증을 수행하고, 성공하면 payload(claims)를 반환한다.
     */
    private Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.", e);
            throw new ExpiredTokenException();
        } catch (JwtException | IllegalArgumentException e) {
            log.info("유효하지 않은 JWT 토큰입니다.", e);
            throw new InvalidTokenException("토큰 형식 또는 서명이 올바르지 않습니다.");
        }
    }
}
