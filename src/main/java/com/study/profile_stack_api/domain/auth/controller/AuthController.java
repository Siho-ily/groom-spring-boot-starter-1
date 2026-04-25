package com.study.profile_stack_api.domain.auth.controller;

import com.study.profile_stack_api.domain.auth.dto.request.LoginRequest;
import com.study.profile_stack_api.domain.auth.dto.request.SignupRequest;
import com.study.profile_stack_api.domain.auth.dto.response.LoginResponse;
import com.study.profile_stack_api.domain.auth.dto.response.SignupResponse;
import com.study.profile_stack_api.domain.auth.service.AuthService;
import com.study.profile_stack_api.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * User signup/registration
     * POST /api/auth/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request) {
        log.info("Signup request for username: {}", request.getUsername());

        SignupResponse response = authService.signup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    /**
     * User login
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for username: {}", request.getUsername());

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }


}
