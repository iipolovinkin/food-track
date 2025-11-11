package com.foodtracker.tracking.service;

import com.foodtracker.tracking.controller.EventRequestDto;
import com.foodtracker.shared.model.Event;

public interface EventService {

    /**
     * Track a new event
     */
    Event trackEvent(EventRequestDto eventRequest);

}