package com.study.profile_stack_api.global.exception;

import com.study.profile_stack_api.global.common.ApiResponse;
import com.study.profile_stack_api.global.error.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateEmailException(DuplicateEmailException e) {
        return ErrorCodeExceptionResponse(e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ApiResponse<Void>> handleInternalServerErrorException(InternalServerErrorException e) {
        return ErrorCodeExceptionResponse(e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleProfileNotFoundException(ProfileNotFoundException e) {
        return ErrorCodeExceptionResponse(e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(TechStackNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleTechStackNotFoundException(TechStackNotFoundException e) {
        return ErrorCodeExceptionResponse(e.getErrorCode(), e.getMessage());
    }




    /**
     * IllegalArgumentException 처리
     * 유혀성 검증 실패, 잘못된 요청 등
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("INVALID_INPUT", e.getMessage()));
    }


//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."));
//    }

    private ResponseEntity<ApiResponse<Void>> ErrorCodeExceptionResponse(ErrorCode errorCode, String message) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode.getCode(), message));
    }
}
