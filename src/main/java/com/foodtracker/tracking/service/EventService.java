package com.foodtracker.tracking.service;

import com.foodtracker.dto.EventRequestDto;
import com.foodtracker.shared.model.Event;

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

}