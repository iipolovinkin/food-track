package com.foodtracker.tracking.service;

import com.foodtracker.api.tracking.EventRequest;
import com.foodtracker.shared.repository.Event;

public interface EventService {

    /**
     * Track a new event
     */
    Event trackEvent(EventRequest eventRequest);

}