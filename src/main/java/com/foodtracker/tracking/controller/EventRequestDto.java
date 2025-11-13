package com.foodtracker.tracking.controller;

import com.foodtracker.api.tracking.EventRequest;
import com.foodtracker.core.validation.UnsafeMap;
import com.foodtracker.core.validation.UnsafeString;
import com.foodtracker.core.validation.ValidProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Request DTO for tracking events")
public record EventRequestDto(
        @Schema(description = "Type of event (e.g., screen_viewed, item_added_to_cart)", example = "screen_viewed", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Event type cannot be blank")
        @Size(max = 100, message = "Event type cannot exceed 100 characters")
        @UnsafeString
        String eventType,

        @Schema(description = "Unique identifier of the user", example = "user_123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "User ID cannot be blank")
        @Size(max = 100, message = "User ID cannot exceed 100 characters")
        @UnsafeString
        String userId,

        @Schema(description = "Unique identifier of the session", example = "session_123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Session ID cannot be blank")
        @Size(max = 100, message = "Session ID cannot exceed 100 characters")
        @UnsafeString
        String sessionId,

        @Schema(description = "Timestamp of the event", example = "2024-05-20T18:42:10", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Timestamp cannot be null")
        @PastOrPresent(message = "Timestamp must be in the past or present, not in the future")
        LocalDateTime timestamp,

        @Schema(description = "Additional properties for the event", example = "{\"screen\": \"menu\", \"category\": \"pizza\"}")
        @ValidProperties
        @UnsafeMap
        Map<String, Object> properties
) implements EventRequest {
}