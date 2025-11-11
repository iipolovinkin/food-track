package com.foodtracker.generator.gateway.tracking;

import com.foodtracker.trackingapi.EventRequest;

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