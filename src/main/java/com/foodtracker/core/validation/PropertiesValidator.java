package com.foodtracker.core.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Map;

public class PropertiesValidator implements ConstraintValidator<ValidProperties, Map<String, Object>> {

    public static final int MAX_KEY_LENGTH = 100;
    public static final int MAX_VALUE_LENGTH = 1000;
    public static final String KEY_PATTERN = "^[a-zA-Z0-9_-]+$";

    @Override
    public boolean isValid(Map<String, Object> properties, ConstraintValidatorContext context) {
        if (properties == null) return true;

        return properties.entrySet().stream()
                .allMatch(entry -> isValidKey(entry.getKey()) && isValidValue(entry.getValue()));
    }

    private boolean isValidKey(String key) {
        return key != null && key.length() <= MAX_KEY_LENGTH && key.matches(KEY_PATTERN);
    }

    private boolean isValidValue(Object value) {
        // Validate value: if string, check length; limit nested structures
        if (value instanceof String strValue) {
            return strValue.length() <= MAX_VALUE_LENGTH;
        } else if (value instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> nestedMap = (Map<String, Object>) value;
            // Recursively validate nested maps
            return nestedMap.entrySet().stream()
                    .allMatch(entry -> isValidKey(entry.getKey()) && isValidValue(entry.getValue()));
        }
        return true;
    }
}