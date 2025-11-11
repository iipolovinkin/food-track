package com.foodtracker.tracking.controller;

import com.foodtracker.dto.EventRequestDto;
import com.foodtracker.shared.model.Event;
import com.foodtracker.tracking.service.EventService;
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

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Event Tracking", description = "Endpoints for tracking user events")
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
            @PathVariable
            String eventType) {
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
            @PathVariable
            String userId) {
        List<Event> events = eventService.getEventsByUser(userId);
        return ResponseEntity.ok(events);
    }
}