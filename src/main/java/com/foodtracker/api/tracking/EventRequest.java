package com.foodtracker.api.tracking;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@Schema(description = "Request DTO for tracking events")
public interface EventRequest {

    @Schema(description = "Type of event (e.g., screen_viewed, item_added_to_cart)", example = "screen_viewed", requiredMode = Schema.RequiredMode.REQUIRED)
    String eventType();

    @Schema(description = "Unique identifier of the user", example = "user_123", requiredMode = Schema.RequiredMode.REQUIRED)
    String userId();

    @Schema(description = "Unique identifier of the session", example = "session_123", requiredMode = Schema.RequiredMode.REQUIRED)
    String sessionId();

    @Schema(description = "Timestamp of the event", example = "2024-05-20T18:42:10", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDateTime timestamp();

    @Schema(description = "Additional properties for the event", example = "{\"screen\": \"menu\", \"category\": \"pizza\"}")
    Map<String, Object> properties();

    default Instant getInstantTimestamp() {
        return timestamp().toInstant(ZoneOffset.UTC);
    }
}
