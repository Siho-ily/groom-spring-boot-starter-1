package com.study.profile_stack_api.global.security.config;

import com.study.profile_stack_api.global.security.jwt.JwtAuthenticationEntryPoint;
import com.study.profile_stack_api.global.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 개발 환경에서만 H2 콘솔 관련 예외를 열어준다.
        boolean isDevProfile = environment.acceptsProfiles(Profiles.of("dev"));

        if (isDevProfile) {
            // H2 콘솔은 iframe을 사용하므로 same-origin frame 허용이 필요하다.
            http.headers(headers -> headers.frameOptions(
                    frameOptions -> frameOptions.sameOrigin()
            ));
        }

        http
                // ① REST API이므로 CSRF 비활성화
                .csrf(csrf -> csrf.disable())

                // ② JWT 방식이므로 세션을 사용하지 않음 (Stateless)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // ③ 요청별 인가 규칙
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/swagger-ui.html", "/swagger-ui/**").permitAll();
                    auth.requestMatchers("/v1/api-docs", "/v1/api-docs/**").permitAll();
                    auth.requestMatchers("/error").permitAll();
                    auth.requestMatchers("/api/v1/auth/**").permitAll();    // 회원가입, 로그인은 누구나 접근 가능

                    if (isDevProfile) {
                        // 운영/테스트 환경에서는 H2 콘솔을 별도로 공개하지 않는다.
                        auth.requestMatchers("/h2-console/**").permitAll();
                    }

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
