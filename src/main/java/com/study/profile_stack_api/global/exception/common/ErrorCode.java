package com.study.profile_stack_api.global.exception.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "INVALID_INPUT"),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND"),
    PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "PROFILE_NOT_FOUND"),
    TECH_STACK_NOT_FOUND(HttpStatus.NOT_FOUND, "TECH_STACK_NOT_FOUND"),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "REFRESH_TOKEN_NOT_FOUND"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR"),
    DUPLICATE_MEMBER_ID(HttpStatus.CONFLICT, "DUPLICATE_MEMBER_ID"),
    DUPLICATE_MEMBER_USERNAME(HttpStatus.CONFLICT, "DUPLICATE_MEMBER_USERNAME"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "DUPLICATE_EMAIL"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "EXPIRED_TOKEN"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN"),
    PERMISSION_DENIED(HttpStatus.FORBIDDEN, "PERMISSION_DENIED"),
    FORBIDDEN_OWNER_MISMATCH(HttpStatus.FORBIDDEN, "FORBIDDEN_OWNER_MISMATCH");

    private final HttpStatus status;
    private final String code;

    ErrorCode(HttpStatus status, String code) {
        this.status = status;
        this.code = code;
    }
}