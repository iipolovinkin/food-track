package com.foodtracker.tracking.service;

import com.foodtracker.api.tracking.EventRequest;
import com.foodtracker.shared.repository.Event;
import com.foodtracker.shared.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public Event trackEvent(EventRequest eventRequest) {
        log.debug("Tracking event: type={}, userId={}, timestamp={}",
                eventRequest.eventType(), eventRequest.userId(), eventRequest.timestamp());

        // Create event entity from DTO using Instant timestamp
        Event event = map(eventRequest);

        // Save to database
        Event savedEvent = eventRepository.save(event);
        log.debug("Event saved with ID: {}", savedEvent.getId());

        return savedEvent;
    }

    private static Event map(EventRequest eventRequest) {
        Event event = new Event();
        event.setEventType(eventRequest.eventType());
        event.setUserId(eventRequest.userId());
        event.setSessionId(eventRequest.sessionId());
        event.setTimestamp(eventRequest.getInstantTimestamp()); // Use converted Instant
        event.setProperties(eventRequest.properties());
        return event;
    }

}