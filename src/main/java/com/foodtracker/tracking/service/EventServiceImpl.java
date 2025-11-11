package com.foodtracker.tracking.service;

import com.foodtracker.dto.EventRequestDto;
import com.foodtracker.shared.model.Event;
import com.foodtracker.shared.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public Event trackEvent(EventRequestDto eventRequest) {
        log.debug("Tracking event: type={}, userId={}, timestamp={}",
                eventRequest.eventType(), eventRequest.userId(), eventRequest.timestamp());

        // Create event entity from DTO
        Event event = map(eventRequest);

        // Save to database
        Event savedEvent = eventRepository.save(event);
        log.debug("Event saved with ID: {}", savedEvent.getId());

        return savedEvent;
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public List<Event> getEventsByType(String eventType) {
        return eventRepository.findByEventType(eventType);
    }

    @Override
    public List<Event> getEventsByUser(String userId) {
        return eventRepository.findByUserId(userId);
    }

    private static Event map(EventRequestDto eventRequest) {
        Event event = new Event();
        event.setEventType(eventRequest.eventType());
        event.setUserId(eventRequest.userId());
        event.setSessionId(eventRequest.sessionId());
        event.setTimestamp(eventRequest.timestamp());
        event.setProperties(eventRequest.properties());
        return event;
    }

}