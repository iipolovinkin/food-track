package com.foodtracker.api.analytics;

import java.time.Instant;
import java.util.Map;

public interface TrackEvent {
    Long getId();

    String getEventType();

    String getUserId();

    String getSessionId();

    Instant getTimestamp();

    Map<String, Object> getProperties();
}
