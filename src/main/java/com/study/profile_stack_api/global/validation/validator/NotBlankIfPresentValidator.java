package com.study.profile_stack_api.global.validation.validator;

import com.study.profile_stack_api.global.validation.annotation.NotBlankIfPresent;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotBlankIfPresentValidator implements ConstraintValidator<NotBlankIfPresent, String> {

    /**
     * 값이 존재할 경우 공백 문자열인지 검증
     *
     * @param value 검증할 문자열 값
     * @param context 검증 컨텍스트
     * @return 검증 통과 여부
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // null이면 수정하지 않음
        if (value == null) return true;

        // 값이 있지만 공백만 있는 문자열 false
        return !value.trim().isEmpty();
    }
}