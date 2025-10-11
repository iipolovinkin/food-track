package com.foodtracker.service.impl;

import com.foodtracker.dto.EventRequestDto;
import com.foodtracker.dto.analytics.ConversionFunnelResponse;
import com.foodtracker.model.Event;
import com.foodtracker.repository.EventRepository;
import com.foodtracker.service.EventService;
import com.foodtracker.util.InputSanitizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public Event trackEvent(EventRequestDto eventRequest) {
        log.info("Tracking event: type={}, userId={}, timestamp={}",
                eventRequest.eventType(), eventRequest.userId(), eventRequest.timestamp());

        sanitizeInputs(eventRequest);

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

    @Override
    public long getDistinctUserCountByEventTypeAndDate(String eventType, LocalDateTime fromDate) {
        return eventRepository.countDistinctUsersByEventTypeAndTimestampAfter(eventType, fromDate);
    }

    @Override
    public List<Event> getEventsByTypeAndTimeRange(String eventType, LocalDateTime start, LocalDateTime end) {
        return eventRepository.findByEventTypeAndTimestampBetween(eventType, start, end);
    }

    @Override
    public ConversionFunnelResponse getConversionFunnelAnalytics(String category, LocalDateTime start, LocalDateTime end) {
        // Use database-level filtering for better performance
        long viewedCount = eventRepository.countByEventTypeAndCategoryAndTimestampBetween("item_viewed", category, start, end);
        long addedCount = eventRepository.countByEventTypeAndCategoryAndTimestampBetween("item_added_to_cart", category, start, end);
        long orderedCount = eventRepository.countByEventTypeAndCategoryAndTimestampBetween("order_placed", category, start, end);

        double conversionRate = viewedCount > 0 ? (double) orderedCount / viewedCount * 100 : 0;

        return new ConversionFunnelResponse(
                category,
                viewedCount,
                addedCount,
                orderedCount,
                conversionRate,
                Map.of("timeRange", String.format("%s to %s", start, end))
        );
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

    private static void sanitizeInputs(EventRequestDto eventRequest) {
        if (InputSanitizer.isUnsafeString(eventRequest.eventType())
                || InputSanitizer.isUnsafeString(eventRequest.userId())
                || InputSanitizer.isUnsafeString(eventRequest.sessionId())
                || InputSanitizer.isUnsafeMap(eventRequest.properties())) {
            log.warn("Potentially unsafe input detected in event request: type={}, userId={}",
                    eventRequest.eventType(), eventRequest.userId());
            throw new IllegalArgumentException("Request contains potentially unsafe content");
        }
    }

}