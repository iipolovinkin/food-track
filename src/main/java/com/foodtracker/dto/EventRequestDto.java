package com.foodtracker.dto;

import com.foodtracker.validation.UnsafeString;
import com.foodtracker.validation.ValidProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Map;

public record EventRequestDto(
        @NotBlank(message = "Event type cannot be blank")
        @Size(max = 100, message = "Event type cannot exceed 100 characters")
        @UnsafeString
        String eventType,

        @NotBlank(message = "User ID cannot be blank")
        @Size(max = 100, message = "User ID cannot exceed 100 characters")
        @UnsafeString
        String userId,

        @NotBlank(message = "Session ID cannot be blank")
        @Size(max = 100, message = "Session ID cannot exceed 100 characters")
        @UnsafeString
        String sessionId,

        @NotNull(message = "Timestamp cannot be null")
        @PastOrPresent(message = "Timestamp must be in the past or present, not in the future")
        LocalDateTime timestamp,

        @ValidProperties
        Map<String, Object> properties
) {
}