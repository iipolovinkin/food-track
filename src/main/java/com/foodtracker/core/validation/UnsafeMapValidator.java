package com.foodtracker.core.validation;

import com.foodtracker.core.util.InputSanitizer;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Map;

public class UnsafeMapValidator implements ConstraintValidator<UnsafeMap, Map<String, Object>> {
    @Override
    public boolean isValid(Map<String, Object> map, ConstraintValidatorContext context) {
        if (map == null) return true;

        return !InputSanitizer.isUnsafeMap(map);
    }
}