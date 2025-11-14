package com.foodtracker.tracking.controller;

import com.foodtracker.shared.repository.Event;
import com.foodtracker.tracking.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}