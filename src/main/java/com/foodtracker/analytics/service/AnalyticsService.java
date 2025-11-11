package com.foodtracker.analytics.service;

import com.foodtracker.analytics.dto.ConversionFunnelResponse;
import com.foodtracker.shared.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface AnalyticsService {

    /**
     * Get distinct user count for an event type from a specific date
     */
    long getDistinctUserCountByEventTypeAndDate(String eventType, LocalDateTime fromDate);

    /**
     * Get events by event type within a time range
     */
    List<Event> getEventsByTypeAndTimeRange(String eventType, LocalDateTime start, LocalDateTime end);

    /**
     * Get conversion funnel analytics for a category within a time range
     */
    ConversionFunnelResponse getConversionFunnelAnalytics(String category, LocalDateTime start, LocalDateTime end);

    /**
     * Get all events
     */
    List<Event> getAllEvents();

    /**
     * Get events by event type
     */
    List<Event> getEventsByType(String eventType);

    /**
     * Get events by user ID
     */
    List<Event> getEventsByUser(String userId);

}