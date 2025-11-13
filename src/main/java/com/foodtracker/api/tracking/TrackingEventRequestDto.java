package com.foodtracker.api.tracking;

import java.time.LocalDateTime;
import java.util.Map;

public record TrackingEventRequestDto(
        String eventType,
        String userId,
        String sessionId,
        LocalDateTime timestamp,
        Map<String, Object> properties
) implements EventRequest {
}