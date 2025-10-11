package com.foodtracker.controller;

import com.foodtracker.dto.EventRequestDto;
import com.foodtracker.dto.analytics.ConversionFunnelResponse;
import com.foodtracker.model.Event;
import com.foodtracker.service.EventService;
import com.foodtracker.util.InputSanitizer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/track")
    public ResponseEntity<String> trackEvent(@RequestBody @Valid EventRequestDto eventRequest) {
        try {
            Event savedEvent = eventService.trackEvent(eventRequest);
            return ResponseEntity.ok("Event tracked successfully with ID: " + savedEvent.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid request: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error tracking event: " + e.getMessage());
        }
    }

    @GetMapping("/events")
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/events/{eventType}")
    public ResponseEntity<List<Event>> getEventsByType(@PathVariable String eventType) {
        List<Event> events = eventService.getEventsByType(eventType);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/users/{userId}/events")
    public ResponseEntity<List<Event>> getEventsByUser(@PathVariable String userId) {
        List<Event> events = eventService.getEventsByUser(userId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/analytics/dau")
    public ResponseEntity<Long> getDailyActiveUsers(@RequestParam String eventType,
                                                    @RequestParam String date) {
        try {
            java.time.LocalDate localDate = java.time.LocalDate.parse(date);
            java.time.LocalDateTime startOfDay = localDate.atStartOfDay();
            long dauCount = eventService.getDistinctUserCountByEventTypeAndDate(eventType, startOfDay);
            return ResponseEntity.ok(dauCount);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(0L);
        }
    }

    @GetMapping("/analytics/conversion-funnel")
    public ResponseEntity<ConversionFunnelResponse> getConversionFunnel(@RequestParam String category,
                                                                        @RequestParam String startDate,
                                                                        @RequestParam String endDate) {
        try {
            // Sanitize category parameter to prevent injection attacks
            if (InputSanitizer.isUnsafeString(category)) {
                return ResponseEntity.badRequest().body(null);
            }

            java.time.LocalDateTime startDateTime = java.time.LocalDateTime.parse(startDate);
            java.time.LocalDateTime endDateTime = java.time.LocalDateTime.parse(endDate);

            ConversionFunnelResponse analytics = eventService.getConversionFunnelAnalytics(category, startDateTime, endDateTime);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}