package com.foodtracker.api.analytics;

import java.time.LocalDateTime;
import java.util.Map;

public interface TrackEvent {
    Long getId();

    String getEventType();

    String getUserId();

    String getSessionId();

    LocalDateTime getTimestamp();

    Map<String, Object> getProperties();
}
