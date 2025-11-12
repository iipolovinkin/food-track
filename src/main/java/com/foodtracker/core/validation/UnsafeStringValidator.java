package com.foodtracker.core.validation;

import com.foodtracker.core.util.InputSanitizer;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UnsafeStringValidator implements ConstraintValidator<UnsafeString, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        return InputSanitizer.isSafeString(value);
    }
}