package com.foodtracker.service;

import com.foodtracker.dto.EventRequestDto;
import com.foodtracker.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    /**
     * Track a new event
     */
    Event trackEvent(EventRequestDto eventRequest);

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
    com.foodtracker.dto.analytics.ConversionFunnelResponse getConversionFunnelAnalytics(String category, LocalDateTime start, LocalDateTime end);
}