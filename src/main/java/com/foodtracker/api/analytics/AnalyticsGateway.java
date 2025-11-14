package com.foodtracker.api.analytics;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Gateway interface for analytics operations.
 * Provides methods to interact with analytics endpoints.
 */
public interface AnalyticsGateway {

    /**
     * Get daily active users for a specific event type and date.
     *
     * @param eventType Type of event to analyze
     * @param date      Date to analyze in LocalDate format
     * @return Count of daily active users
     */
    Long getDailyActiveUsers(String eventType, LocalDate date);

    /**
     * Get conversion funnel analytics for a specific category within a date range.
     *
     * @param category  Category to analyze (e.g., pizza, burger)
     * @param startDate Start date and time for the analysis
     * @param endDate   End date and time for the analysis
     * @return Conversion funnel response with analytics data
     */
    ConversionFunnelResponse getConversionFunnel(String category, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get all events from the system
     *
     * @return List of all events
     */
    List<TrackEvent> getAllEvents();

    /**
     * Get events filtered by event type
     *
     * @param eventType Type of event to filter by
     * @return List of events matching the type
     */
    List<TrackEvent> getEventsByType(String eventType);

    /**
     * Get events for a specific user
     *
     * @param userId ID of the user to retrieve events for
     * @return List of events for the specified user
     */
    List<TrackEvent> getEventsByUser(String userId);
}