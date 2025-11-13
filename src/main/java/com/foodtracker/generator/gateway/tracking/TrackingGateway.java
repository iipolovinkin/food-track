package com.foodtracker.generator.gateway.tracking;

import com.foodtracker.api.tracking.TrackingEventRequestDto;

public interface TrackingGateway {

    /**
     * Send an event to the tracking API.
     */
    boolean sendEvent(TrackingEventRequestDto event);

    /**
     * Perform basic validation of the event.
     */
    boolean validateEvent(TrackingEventRequestDto event);
}
