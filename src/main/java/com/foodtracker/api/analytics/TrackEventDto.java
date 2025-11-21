package com.foodtracker.api.analytics;

import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
public class TrackEventDto implements TrackEvent {

    private Long id;

    private String eventType;

    private String userId;

    private String sessionId;

    private Instant timestamp;

    private Map<String, Object> properties;
}