package com.study.profile_stack_api.global.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    // success: 처리 성공 여부
    // code: 응답 코드
    // message: 응답 메세지
    // data: 응답 데이터
    private boolean success;
    private String code;
    private String message;
    private T data;

    // 성공
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "OK", null, data);
    }

    // 실패
    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }

    public static <T> ApiResponse<T> error(String code, String message, T data) {
        return new ApiResponse<>(false, code, message, data);
    }
}
