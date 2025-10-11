package com.foodtracker.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Map;

public class PropertiesValidator implements ConstraintValidator<ValidProperties, Map<String, Object>> {
    @Override
    public boolean isValid(Map<String, Object> properties, ConstraintValidatorContext context) {
        if (properties == null) return true;
        
        return properties.entrySet().stream()
            .allMatch(entry -> isValidKey(entry.getKey()) && isValidValue(entry.getValue()));
    }
    
    private boolean isValidKey(String key) {
        // Validate key: not null, reasonable length, and safe characters
        return key != null && key.length() <= 100 && key.matches("^[a-zA-Z0-9_-]+$");
    }
    
    private boolean isValidValue(Object value) {
        // Validate value: if string, check length; limit nested structures
        if (value instanceof String) {
            String strValue = (String) value;
            return strValue.length() <= 1000; // Prevent extremely large values
        } else if (value instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> nestedMap = (Map<String, Object>) value;
            // Recursively validate nested maps
            return nestedMap.entrySet().stream()
                .allMatch(entry -> isValidKey(entry.getKey()) && isValidValue(entry.getValue()));
        }
        // Allow other types (numbers, booleans, etc.)
        return true;
    }
}