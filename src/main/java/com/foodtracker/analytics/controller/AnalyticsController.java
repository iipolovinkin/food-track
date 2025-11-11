package com.foodtracker.analytics.controller;

import com.foodtracker.analytics.dto.ConversionFunnelResponse;
import com.foodtracker.analytics.service.AnalyticsService;
import com.foodtracker.shared.model.Event;
import com.foodtracker.validation.UnsafeString;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Endpoints for analytics and reporting")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Operation(
            summary = "Get daily active users",
            description = "Retrieves the count of unique users who performed a specific event type on a given date",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Daily active user count retrieved successfully",
                            content = @Content(schema = @Schema(implementation = Long.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request parameters")
            }
    )
    @GetMapping("/dau")
    public ResponseEntity<Long> getDailyActiveUsers(
            @Parameter(description = "Type of event to analyze", required = true)
            @RequestParam String eventType,
            @Parameter(description = "Date in ISO format (e.g., 2024-01-01)", required = true)
            @RequestParam String date) {
        try {
            java.time.LocalDate localDate = java.time.LocalDate.parse(date);
            java.time.LocalDateTime startOfDay = localDate.atStartOfDay();
            long dauCount = analyticsService.getDistinctUserCountByEventTypeAndDate(eventType, startOfDay);
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
    @GetMapping("/conversion-funnel")
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

            ConversionFunnelResponse analytics = analyticsService.getConversionFunnelAnalytics(category, startDateTime, endDateTime);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
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
        List<Event> events = analyticsService.getAllEvents();
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
        List<Event> events = analyticsService.getEventsByType(eventType);
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
        List<Event> events = analyticsService.getEventsByUser(userId);
        return ResponseEntity.ok(events);
    }
}