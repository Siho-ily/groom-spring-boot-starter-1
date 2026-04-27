package com.study.profile_stack_api.global.security.config;

import com.study.profile_stack_api.global.security.jwt.JwtAuthenticationEntryPoint;
import com.study.profile_stack_api.global.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final Environment environment;

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        // 개발 환경에서만 H2 콘솔 관련 예외를 열어준다.
        boolean isDevProfile = environment.acceptsProfiles(Profiles.of("dev"));

        if (isDevProfile) {
            // H2 콘솔은 iframe을 사용하므로 same-origin frame 허용이 필요하다.
            http.headers(headers -> headers.frameOptions(
                    HeadersConfigurer.FrameOptionsConfig::sameOrigin
            ));
        }

        http
                // ① REST API이므로 CSRF 비활성화
                .csrf(AbstractHttpConfigurer::disable)

                // ② JWT 방식이므로 세션을 사용하지 않음 (Stateless)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // ③ 요청별 인가 규칙
                .authorizeHttpRequests(auth -> {
                    // *** permitAll ***
                    // swagger나 문서 접근 허용
                    auth.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v1/api-docs/**").permitAll();

                    // 회원가입, 로그인, 토큰 재발급은 로그인 전에도 허용
                    auth.requestMatchers("/api/v1/auth/signup", "/api/v1/auth/login", "/api/v1/auth/refresh").permitAll();

                    // 프로피일 조회는 허용
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/profiles", "/api/v1/profiles/*").permitAll();

                    // 태크스택 조회는 허용
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/profiles/*/tech-stacks", "/api/v1/profiles/*/tech-stacks/*").permitAll();

                    // 운영/테스트 환경에서는 H2 콘솔을 별도로 공개하지 않음
                    if (isDevProfile) {
                        auth.requestMatchers("/h2-console/**").permitAll();
                    }


                    // *** authenticated ***
                    // 로그아웃은 로그인한 사용자만 허용
                    auth.requestMatchers("/api/v1/auth/logout").authenticated();

                    // 프로파일 생성, 수정, 삭제는 로그인한 사용자만 허용
                    auth.requestMatchers(HttpMethod.POST, "/api/v1/profiles").authenticated();
                    auth.requestMatchers(HttpMethod.PUT, "/api/v1/profiles/*").authenticated();
                    auth.requestMatchers(HttpMethod.DELETE, "/api/v1/profiles/*").authenticated();

                    // 태크스택 생성, 수정, 삭제는 로그인한 사용자만 허용
                    auth.requestMatchers(HttpMethod.POST, "/api/v1/profiles/*/tech-stacks").authenticated();
                    auth.requestMatchers(HttpMethod.PUT, "/api/v1/profiles/*/tech-stacks/*").authenticated();
                    auth.requestMatchers(HttpMethod.DELETE, "/api/v1/profiles/*/tech-stacks/*").authenticated();

                    auth.anyRequest().authenticated();                          // 나머지는 인증 필요
                })

                // ④ 인증 실패 시 커스텀 EntryPoint 사용
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )

                // ⑤ JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 배치
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
