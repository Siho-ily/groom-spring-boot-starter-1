package com.study.profile_stack_api.global.exception;

import com.study.profile_stack_api.global.common.ApiResponse;
import com.study.profile_stack_api.global.exception.common.ErrorCode;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.exc.InvalidFormatException;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 커스텀 비즈니스 예외
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode.getCode(), e.getMessage()));
    }


    /**
     * PathVariable, RequestParams 검증 예외
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<List<ValidationErrorDetail>>> handleConstraintViolationException(ConstraintViolationException e) {
        List<ValidationErrorDetail> errors = e.getConstraintViolations()
                .stream()
                .map(violation -> new ValidationErrorDetail(
                        extractFieldName(violation.getPropertyPath().toString()),
                        violation.getMessage()
                ))
                .toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("INVALID_INPUT", "요청 파라미터 값이 유효하지 않습니다.", errors));
    }

    /**
     * RequestBody  검증 예외
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<ValidationErrorDetail>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ValidationErrorDetail> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ValidationErrorDetail(
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("INVALID_INPUT", "요청 바디의 필드 값이 유효하지 않습니다.",errors));
    }

    /**
     * RequestBody JSON 파싱/타입 변환 예외
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<List<ValidationErrorDetail>>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        // RequestBody 안에서 enum 등 타입 변환이 실패하면 Jackson 예외가 원인(cause)으로 들어온다.
        // 따라서, 겉 Spring 예외(포장지)를 벗겨내어 진짜 예외(Jackson 예외)를 꺼내는 과정
        Throwable cause = e.getCause();

        // 포메팅 에러가 발생했을 때 들어옴(Enum 필드에 허용되지 않은 문자열이 들어온 경우)
        if (cause instanceof InvalidFormatException invalidFormatException) {
            //invalidFormatException.getTargetType() "변환하려던 최종 타입이 뭐였는지 알려주는 값"
            Class<?> targetType = invalidFormatException.getTargetType();

            // 타겟 타입이 없거나,
            if (targetType != null && targetType.isEnum()) {
                String fieldName = extractFieldName(invalidFormatException);
                String allowedValues = buildAllowedEnumValues(targetType);

                List<ValidationErrorDetail> errors = List.of(new ValidationErrorDetail(
                        fieldName,
                        String.format("허용되지 않은 값입니다. 가능한 값: %s", allowedValues)
                ));

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("INVALID_INPUT", "요청 바디의 필드 값이 유효하지 않습니다.", errors));
            }
        }

        // Enum 외의 JSON 파싱 오류는 범용 메시지로 응답한다.
        // * 이수 수정 필요함 *
        log.info("HttpMessageNotReadableException 예외 처리 중, Enum 이외의 예외 발생함: {}", e.getMessage());
        List<ValidationErrorDetail> errors = List.of(new ValidationErrorDetail(
                "requestBody",
                "요청 바디를 읽을 수 없습니다."
        ));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("INVALID_INPUT", "요청 바디 형식이 올바르지 않습니다.", errors));
    }

    /**
     * RequestParam, PathVariable 타입 변환 예외
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<List<ValidationErrorDetail>>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        List<ValidationErrorDetail> errors = List.of(new ValidationErrorDetail(
                e.getName(),
                buildTypeMismatchMessage(e)
        ));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("INVALID_INPUT", "요청 파라미터 값이 유효하지 않습니다.", errors));
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

    private String extractFieldName(String propertyPath) {
        int lastDotIndex = propertyPath.lastIndexOf('.');
        if (lastDotIndex < 0 || lastDotIndex == propertyPath.length() - 1) {
            return propertyPath;
        }
        return propertyPath.substring(lastDotIndex + 1);
    }

    private String extractFieldName(InvalidFormatException e) {
        // Jackson이 제공하는 Reference API로 마지막 JSON 필드명만 꺼낸다.
        if (e.getPath() == null || e.getPath().isEmpty()) {
            return "unknown";
        }

        var reference = e.getPath().get(e.getPath().size() - 1);

        if (reference.getPropertyName() != null) {
            return reference.getPropertyName();
        }

        if (reference.getIndex() >= 0) {
            return String.valueOf(reference.getIndex());
        }

        return "unknown";
    }

    private String buildTypeMismatchMessage(MethodArgumentTypeMismatchException e) {
        Class<?> requiredType = e.getRequiredType();
        if (requiredType != null && requiredType.isEnum()) {
            return String.format("허용되지 않은 값입니다. 가능한 값: %s", buildAllowedEnumValues(requiredType));
        }

        return "요청 파라미터 타입이 올바르지 않습니다.";
    }

    private String buildAllowedEnumValues(Class<?> enumType) {
        // Enum 상수 목록을 메시지에 재사용할 수 있게 별도 메서드로 분리했다.
        return Arrays.stream(enumType.getEnumConstants())
                .map(String::valueOf)
                .reduce((left, right) -> left + ", " + right)
                .orElse("");
    }


//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."));
//    }

    public record ValidationErrorDetail(String field, String message) {}
}
