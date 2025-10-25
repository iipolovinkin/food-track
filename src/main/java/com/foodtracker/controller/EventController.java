package com.foodtracker.controller;

import com.foodtracker.dto.EventRequestDto;
import com.foodtracker.dto.analytics.ConversionFunnelResponse;
import com.foodtracker.model.Event;
import com.foodtracker.service.EventService;
import com.foodtracker.validation.UnsafeString;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Event Tracking", description = "Endpoints for tracking user events and analytics")
public class EventController {

    private final EventService eventService;

    @Operation(
        summary = "Track a new event",
        description = "Records a user event with details like event type, user ID, session ID, timestamp, and properties",
        responses = {
            @ApiResponse(responseCode = "200", description = "Event tracked successfully", 
                content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    @PostMapping("/track")
    public ResponseEntity<String> trackEvent(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Event tracking request with event details",
            required = true,
            content = @Content(schema = @Schema(implementation = EventRequestDto.class))
        )
        @RequestBody @Valid EventRequestDto eventRequest) {
        try {
            Event savedEvent = eventService.trackEvent(eventRequest);
            return ResponseEntity.ok("Event tracked successfully with ID: " + savedEvent.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid request: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error tracking event: " + e.getMessage());
        }
    }

    @Operation(
        summary = "Get all events",
        description = "Retrieves a list of all recorded events in the system",
        responses = {
            @ApiResponse(responseCode = "200", description = "List of events retrieved successfully", 
                content = @Content(schema = @Schema(implementation = Event.class)))
        }
    )
    @GetMapping("/events")
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @Operation(
        summary = "Get events by type",
        description = "Retrieves a list of events filtered by event type",
        responses = {
            @ApiResponse(responseCode = "200", description = "List of events retrieved successfully", 
                content = @Content(schema = @Schema(implementation = Event.class))),
            @ApiResponse(responseCode = "400", description = "Invalid event type parameter")
        }
    )
    @GetMapping("/events/{eventType}")
    public ResponseEntity<List<Event>> getEventsByType(
        @Parameter(description = "Type of event to filter by (e.g., screen_viewed, item_added_to_cart)", required = true)
        @PathVariable String eventType) {
        List<Event> events = eventService.getEventsByType(eventType);
        return ResponseEntity.ok(events);
    }

    @Operation(
        summary = "Get events by user",
        description = "Retrieves a list of events for a specific user",
        responses = {
            @ApiResponse(responseCode = "200", description = "List of events retrieved successfully", 
                content = @Content(schema = @Schema(implementation = Event.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user ID parameter")
        }
    )
    @GetMapping("/users/{userId}/events")
    public ResponseEntity<List<Event>> getEventsByUser(
        @Parameter(description = "ID of the user to retrieve events for", required = true)
        @PathVariable String userId) {
        List<Event> events = eventService.getEventsByUser(userId);
        return ResponseEntity.ok(events);
    }

    @Operation(
        summary = "Get daily active users",
        description = "Retrieves the count of unique users who performed a specific event type on a given date",
        responses = {
            @ApiResponse(responseCode = "200", description = "Daily active user count retrieved successfully", 
                content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
        }
    )
    @GetMapping("/analytics/dau")
    public ResponseEntity<Long> getDailyActiveUsers(
        @Parameter(description = "Type of event to analyze", required = true)
        @RequestParam String eventType,
        @Parameter(description = "Date in ISO format (e.g., 2024-01-01)", required = true)
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

    @Operation(
        summary = "Get conversion funnel analytics",
        description = "Retrieves conversion funnel metrics for a specific category within a date range",
        responses = {
            @ApiResponse(responseCode = "200", description = "Conversion funnel analytics retrieved successfully", 
                content = @Content(schema = @Schema(implementation = ConversionFunnelResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
        }
    )
    @GetMapping("/analytics/conversion-funnel")
    public ResponseEntity<ConversionFunnelResponse> getConversionFunnel(
        @Parameter(description = "Category to analyze (e.g., pizza, burger)", required = true)
        @RequestParam
        @Valid
        @UnsafeString String category,
        @Parameter(description = "Start date and time in ISO format (e.g., 2024-01-01T10:00:00)", required = true)
        @RequestParam String startDate,
        @Parameter(description = "End date and time in ISO format (e.g., 2024-01-02T10:00:00)", required = true)
        @RequestParam String endDate) {
        try {
            LocalDateTime startDateTime = LocalDateTime.parse(startDate);
            LocalDateTime endDateTime = LocalDateTime.parse(endDate);

            ConversionFunnelResponse analytics = eventService.getConversionFunnelAnalytics(category, startDateTime, endDateTime);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}