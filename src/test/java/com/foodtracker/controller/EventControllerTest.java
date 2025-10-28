package com.foodtracker.controller;

import com.foodtracker.dto.EventRequestDto;
import com.foodtracker.dto.analytics.ConversionFunnelResponse;
import com.foodtracker.model.Event;
import com.foodtracker.service.EventService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

class EventControllerTest {
    @Mock
    EventService eventService;
    @InjectMocks
    EventController eventController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testTrackEvent() {
        when(eventService.trackEvent(any(EventRequestDto.class))).thenReturn(new Event());

        ResponseEntity<String> result = eventController.trackEvent(new EventRequestDto("eventType", "userId", "sessionId", LocalDateTime.of(2025, Month.OCTOBER, 24, 23, 44, 9), Map.of("properties", "properties")));
        Assertions.assertEquals(new ResponseEntity<String>("body", null, 0), result);
    }

    @Test
    void testGetAllEvents() {
        when(eventService.getAllEvents()).thenReturn(List.of(new Event()));

        ResponseEntity<List<Event>> result = eventController.getAllEvents();
        Assertions.assertEquals(new ResponseEntity<>(List.of(new Event()), null, 0), result);
    }

    @Test
    void testGetEventsByType() {
        when(eventService.getEventsByType(anyString())).thenReturn(List.of(new Event()));

        ResponseEntity<List<Event>> result = eventController.getEventsByType("eventType");
        Assertions.assertEquals(new ResponseEntity<>(List.of(new Event()), null, 0), result);
    }

    @Test
    void testGetEventsByUser() {
        when(eventService.getEventsByUser(anyString())).thenReturn(List.of(new Event()));

        ResponseEntity<List<Event>> result = eventController.getEventsByUser("userId");
        Assertions.assertEquals(new ResponseEntity<>(List.of(new Event()), null, 0), result);
    }

    @Test
    void testGetDailyActiveUsers() {
        when(eventService.getDistinctUserCountByEventTypeAndDate(anyString(), any(LocalDateTime.class))).thenReturn(0L);

        ResponseEntity<Long> result = eventController.getDailyActiveUsers("eventType", "date");
        Assertions.assertEquals(new ResponseEntity<>(Long.valueOf(1), null, 0), result);
    }

    @Test
    void testGetConversionFunnel() {
        when(eventService.getConversionFunnelAnalytics(anyString(), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(new ConversionFunnelResponse("category", 0L, 0L, 0L, 0d, Map.of("additionalMetrics", "additionalMetrics")));

        ResponseEntity<ConversionFunnelResponse> result = eventController.getConversionFunnel("category", "startDate", "endDate");
        Assertions.assertEquals(new ResponseEntity<>(new ConversionFunnelResponse("category", 0L, 0L, 0L, 0d, Map.of("additionalMetrics", "additionalMetrics")), null, 0), result);
    }
}