package com.study.profile_stack_api.global.security.jwt;


import com.study.profile_stack_api.global.exception.domain.auth.ExpiredTokenException;
import com.study.profile_stack_api.global.exception.domain.auth.InvalidTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JWT Token Provider
 * Generates, validates, and parses JWT tokens using JJWT 0.12.x API
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity}") long accessTokenValidity,
            @Value("${jwt.refresh-token-validity}") long refreshTokenValidity) {

        // Create a secret key from the configured secret string
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityInMilliseconds = accessTokenValidity * 1000; // Convert to milliseconds
        this.refreshTokenValidityInMilliseconds = refreshTokenValidity * 1000; // Convert to milliseconds
    }

    /**
     * Generate access token from Authentication
     */
    public String generateAccessToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return generateAccessToken(userDetails.getUsername(), userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(",")));
    }

    /**
     * Generate access token from username and roles
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
     * Generate refresh token from username
     */
    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenValidityInMilliseconds);

        return Jwts.builder()
                .subject(username)
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Extract username from token
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getSubject();
        } catch (Exception e) {
            log.error("Failed to extract username from token", e);
            throw new InvalidTokenException("Invalid token");
        }
    }

    /**
     * Extract roles from token
     */
    public String getRolesFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.get("roles", String.class);
        } catch (Exception e) {
            log.error("Failed to extract roles from token", e);
            return "";
        }
    }

    /**
     * Validate token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("Token has expired", e);
            throw new ExpiredTokenException();
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token", e);
            throw new InvalidTokenException("지원하지 않는 토큰");
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token format", e);
            throw new InvalidTokenException("유효하지 않은 토큰 포멧");
        } catch (SignatureException e) {
            log.error("Invalid JWT signature", e);
            throw new InvalidTokenException("유효하지 않은 토큰 시그니처");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty", e);
            throw new InvalidTokenException("JWT claims 문자열이 비어있습니다.");
        }
    }

    /**
     * Validate if token is a refresh token
     */
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String type = claims.get("type", String.class);
            return "refresh".equals(type);
        } catch (Exception e) {
            log.error("Failed to check token type", e);
            return false;
        }
    }

    /**
     * Get token expiration time
     */
    public Date getExpirationFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getExpiration();
        } catch (Exception e) {
            log.error("Failed to extract expiration from token", e);
            throw new InvalidTokenException("Invalid token");
        }
    }
}
