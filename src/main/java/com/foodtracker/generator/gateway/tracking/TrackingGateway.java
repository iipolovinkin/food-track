package com.foodtracker.generator.gateway.tracking;

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
