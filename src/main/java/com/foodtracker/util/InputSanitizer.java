package com.foodtracker.util;

import java.util.Collection;
import java.util.Map;

public class InputSanitizer {

    /**
     * Validates if the input string is safe (no malicious code)
     *
     * @param input The string to validate
     * @return true if input is safe, false otherwise
     */
    public static boolean isSafeString(String input) {
        return !isUnsafeString(input);
    }

    /**
     * Validates if the input string is unsafe (malicious code)
     *
     * @param input The string to validate
     * @return true if input is unsafe, false otherwise
     */
    public static boolean isUnsafeString(String input) {
        if (input == null) {
            return false;
        }

        // Check for potential code injection patterns
        String lowerInput = input.toLowerCase();
        return lowerInput.contains("<script")
                || lowerInput.contains("javascript:")
                || lowerInput.contains("vbscript:")
                || lowerInput.contains("onerror")
                || lowerInput.contains("onload")
                || lowerInput.contains("eval(")
                || lowerInput.contains("expression(");
    }

    /**
     * Validates if a map and its contents are unsafe
     *
     * @param map The map to validate
     * @return true if map is unsafe, false otherwise
     */
    public static boolean isUnsafeMap(Map<String, Object> map) {
        if (map == null) {
            return false;
        }

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            // Validate key
            if (isUnsafeString(entry.getKey())) {
                return true;
            }

            // Validate value
            Object value = entry.getValue();
            if (value instanceof String string) {
                return isUnsafeString(string);
            } else if (value instanceof Map) {
                return isUnsafeMap((Map<String, Object>) value);
            } else if (value instanceof Collection<?> collection) {
                // For collections, we'll do a basic check
                for (Object item : collection) {
                    if (item instanceof String && isUnsafeString((String) item)) {
                        return true;
                    } else if (item instanceof Map nestedMap) {
                        return isUnsafeMap((Map<String, Object>) nestedMap);
                    }
                }
            }
            // Other types (numbers, booleans) are considered safe
        }

        return false;
    }
}