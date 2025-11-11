package com.foodtracker.tracking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodtracker.FoodTrackerApplication;
import com.foodtracker.dto.EventRequestDto;
import com.foodtracker.shared.model.Event;
import com.foodtracker.shared.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@Tag("container")
@Testcontainers
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {FoodTrackerApplication.class})
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EventControllerIntegration2Test {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("foodtracker_test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        // Clean up any existing events before each test
        eventRepository.deleteAll();
    }

    @Test
    void trackEvent_ValidEvent_ReturnsSuccess() throws Exception {
        // Given
        EventRequestDto eventRequestDto = new EventRequestDto(
                "screen_viewed",
                "user_123",
                "session_456",
                LocalDateTime.now(),
                Map.of("screen", "menu", "category", "pizza")
        );

        // When & Then
        performTrackEvent(eventRequestDto)
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Event tracked successfully with ID:")));

        // Verify event was saved to database
        List<Event> events = eventRepository.findAll();
        assertThat(events).hasSize(1);
        Event savedEvent = events.getFirst();
        assertThat(savedEvent.getEventType()).isEqualTo("screen_viewed");
        assertThat(savedEvent.getUserId()).isEqualTo("user_123");
        assertThat(savedEvent.getSessionId()).isEqualTo("session_456");
        assertThat(savedEvent.getProperties()).containsEntry("screen", "menu").containsEntry("category", "pizza");
    }

    @Test
    void trackEvent_MissingRequiredFields_ReturnsBadRequest() throws Exception {
        // Given
        // Empty DTO to trigger validation errors
        EventRequestDto eventRequestDto = new EventRequestDto(
                "",  // Invalid - empty event type
                "",  // Invalid - empty user ID
                "",  // Invalid - empty session ID
                null, // Invalid - null timestamp
                null // Valid - properties can be null
        );

        // When & Then
        performTrackEvent(eventRequestDto)
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Event type cannot be blank")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Session ID cannot be blank")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("User ID cannot be blank")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Timestamp cannot be null")));
    }

    private ResultActions performTrackEvent(EventRequestDto eventRequestDto) throws Exception {
        return mockMvc.perform(post("/api/track")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventRequestDto)));
    }

    @Test
    void trackEvent_InvalidFutureTimestamp_ReturnsBadRequest() throws Exception {
        // Given
        // DTO with future timestamp to trigger validation error
        EventRequestDto eventRequestDto = new EventRequestDto(
                "screen_viewed",
                "user_123",
                "session_456",
                LocalDateTime.now().plusDays(1), // Invalid - future timestamp
                Map.of("screen", "menu")
        );

        // When & Then
        performTrackEvent(eventRequestDto)
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Timestamp must be in the past or present, not in the future")));
    }

    @Test
    void trackEvent_EventIsSavedToDatabase() throws Exception {
        // Given
        int initialCount = eventRepository.findAll().size();
        EventRequestDto eventRequestDto = new EventRequestDto(
                "item_viewed",
                "user_789",
                "session_abc",
                LocalDateTime.now(),
                Map.of("itemId", "pizza_123", "category", "pizza")
        );

        // When
        performTrackEvent(eventRequestDto);

        // Then
        List<Event> events = eventRepository.findAll();
        assertThat(events).hasSize(initialCount + 1);
        Event savedEvent = events.stream()
                .filter(event -> event.getUserId().equals("user_789"))
                .findFirst()
                .orElse(null);
        assertThat(savedEvent).isNotNull();
        assertThat(savedEvent.getEventType()).isEqualTo("item_viewed");
        assertThat(savedEvent.getSessionId()).isEqualTo("session_abc");
        assertThat(savedEvent.getProperties()).containsEntry("itemId", "pizza_123").containsEntry("category", "pizza");
    }

    @Test
    void trackEvent_SpecialCharactersInProperties_ReturnsSuccess() throws Exception {
        // Given
        EventRequestDto eventRequestDto = new EventRequestDto(
                "screen_viewed",
                "user_special!@#$%",
                "session_456",
                LocalDateTime.now(),
                Map.of(
                        "screen", "menu",
                        "category", "burger & pizza",
                        "description", "Special chars: àáâãäåæçèé"
                )
        );

        // When & Then
        performTrackEvent(eventRequestDto)
                .andExpect(status().isOk());

        // Verify event was saved with special characters
        List<Event> events = eventRepository.findAll();
        Event savedEvent = events.getLast(); // Last added event
        assertThat(savedEvent.getUserId()).isEqualTo("user_special!@#$%");
        assertThat(savedEvent.getProperties())
                .containsEntry("category", "burger & pizza")
                .containsEntry("description", "Special chars: àáâãäåæçèé");
    }

    @Test
    void trackEvent_ResponseCodeAndContentVerification() throws Exception {
        // Given
        EventRequestDto eventRequestDto = new EventRequestDto(
                "order_placed",
                "user_999",
                "session_888",
                LocalDateTime.now(),
                Map.of("orderId", "order_123", "amount", 29.99)
        );

        // When & Then
        performTrackEvent(eventRequestDto)
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8")) // Response is a string
                .andExpect(content().string(org.hamcrest.Matchers.startsWith("Event tracked successfully")));

        // Additional verification of database persistence
        List<Event> events = eventRepository.findAll();
        assertThat(events).isNotEmpty();
        Event latestEvent = events.getLast();
        assertThat(latestEvent.getEventType()).isEqualTo("order_placed");
        assertThat(latestEvent.getProperties()).containsEntry("orderId", "order_123").containsEntry("amount", 29.99);
    }
}