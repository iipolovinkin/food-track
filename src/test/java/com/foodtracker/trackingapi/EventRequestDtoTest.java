package com.foodtracker.trackingapi;

import com.foodtracker.tracking.controller.EventRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EventRequestDtoTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    @DisplayName("Valid EventRequestDto should pass validation")
    void testValidEventRequestDto() {
        EventRequestDto eventRequestDto = new EventRequestDto(
                "screen_viewed",
                "user123",
                "session456",
                LocalDateTime.now(),
                Collections.singletonMap("screen", "menu")
        );

        Set<ConstraintViolation<EventRequestDto>> violations = validator.validate(eventRequestDto);
        
        assertTrue(violations.isEmpty(), "Valid EventRequestDto should have no validation errors");
    }

    @Test
    @DisplayName("Event type cannot be null")
    void testEventTypeCannotBeNull() {
        EventRequestDto eventRequestDto = new EventRequestDto(
                null,
                "user123",
                "session456",
                LocalDateTime.now(),
                Collections.emptyMap()
        );

        Set<ConstraintViolation<EventRequestDto>> violations = validator.validate(eventRequestDto);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("eventType") && 
                        violation.getMessage().equals("Event type cannot be blank")));
    }

    @Test
    @DisplayName("Event type cannot be blank")
    void testEventTypeCannotBeBlank() {
        EventRequestDto eventRequestDto = new EventRequestDto(
                "   ",
                "user123",
                "session456",
                LocalDateTime.now(),
                Collections.emptyMap()
        );

        Set<ConstraintViolation<EventRequestDto>> violations = validator.validate(eventRequestDto);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("eventType") && 
                        violation.getMessage().equals("Event type cannot be blank")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"}) // 101 characters
    @DisplayName("Event type cannot exceed 100 characters")
    void testEventTypeCannotExceedMaxLength(String longEventType) {
        EventRequestDto eventRequestDto = new EventRequestDto(
                longEventType,
                "user123",
                "session456",
                LocalDateTime.now(),
                Collections.emptyMap()
        );

        Set<ConstraintViolation<EventRequestDto>> violations = validator.validate(eventRequestDto);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("eventType") && 
                        violation.getMessage().equals("Event type cannot exceed 100 characters")));
    }

    @Test
    @DisplayName("User ID cannot be null")
    void testUserIdCannotBeNull() {
        EventRequestDto eventRequestDto = new EventRequestDto(
                "screen_viewed",
                null,
                "session456",
                LocalDateTime.now(),
                Collections.emptyMap()
        );

        Set<ConstraintViolation<EventRequestDto>> violations = validator.validate(eventRequestDto);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("userId") && 
                        violation.getMessage().equals("User ID cannot be blank")));
    }

    @Test
    @DisplayName("User ID cannot be blank")
    void testUserIdCannotBeBlank() {
        EventRequestDto eventRequestDto = new EventRequestDto(
                "screen_viewed",
                "   ",
                "session456",
                LocalDateTime.now(),
                Collections.emptyMap()
        );

        Set<ConstraintViolation<EventRequestDto>> violations = validator.validate(eventRequestDto);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("userId") && 
                        violation.getMessage().equals("User ID cannot be blank")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"}) // 101 characters
    @DisplayName("User ID cannot exceed 100 characters")
    void testUserIdCannotExceedMaxLength(String longUserId) {
        EventRequestDto eventRequestDto = new EventRequestDto(
                "screen_viewed",
                longUserId,
                "session456",
                LocalDateTime.now(),
                Collections.emptyMap()
        );

        Set<ConstraintViolation<EventRequestDto>> violations = validator.validate(eventRequestDto);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("userId") && 
                        violation.getMessage().equals("User ID cannot exceed 100 characters")));
    }

    @Test
    @DisplayName("Session ID cannot be null")
    void testSessionIdCannotBeNull() {
        EventRequestDto eventRequestDto = new EventRequestDto(
                "screen_viewed",
                "user123",
                null,
                LocalDateTime.now(),
                Collections.emptyMap()
        );

        Set<ConstraintViolation<EventRequestDto>> violations = validator.validate(eventRequestDto);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("sessionId") && 
                        violation.getMessage().equals("Session ID cannot be blank")));
    }

    @Test
    @DisplayName("Session ID cannot be blank")
    void testSessionIdCannotBeBlank() {
        EventRequestDto eventRequestDto = new EventRequestDto(
                "screen_viewed",
                "user123",
                "   ",
                LocalDateTime.now(),
                Collections.emptyMap()
        );

        Set<ConstraintViolation<EventRequestDto>> violations = validator.validate(eventRequestDto);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("sessionId") && 
                        violation.getMessage().equals("Session ID cannot be blank")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"}) // 101 characters
    @DisplayName("Session ID cannot exceed 100 characters")
    void testSessionIdCannotExceedMaxLength(String longSessionId) {
        EventRequestDto eventRequestDto = new EventRequestDto(
                "screen_viewed",
                "user123",
                longSessionId,
                LocalDateTime.now(),
                Collections.emptyMap()
        );

        Set<ConstraintViolation<EventRequestDto>> violations = validator.validate(eventRequestDto);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("sessionId") && 
                        violation.getMessage().equals("Session ID cannot exceed 100 characters")));
    }

    @Test
    @DisplayName("Timestamp cannot be null")
    void testTimestampCannotBeNull() {
        EventRequestDto eventRequestDto = new EventRequestDto(
                "screen_viewed",
                "user123",
                "session456",
                null,
                Collections.emptyMap()
        );

        Set<ConstraintViolation<EventRequestDto>> violations = validator.validate(eventRequestDto);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("timestamp") && 
                        violation.getMessage().equals("Timestamp cannot be null")));
    }

    @Test
    @DisplayName("Timestamp in future should fail validation")
    void testFutureTimestampFailsValidation() {
        EventRequestDto eventRequestDto = new EventRequestDto(
                "screen_viewed",
                "user123",
                "session456",
                LocalDateTime.now().plusHours(1), // Future timestamp
                Collections.emptyMap()
        );

        Set<ConstraintViolation<EventRequestDto>> violations = validator.validate(eventRequestDto);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("timestamp") && 
                        violation.getMessage().equals("Timestamp must be in the past or present, not in the future")));
    }

    @Test
    @DisplayName("Timestamp in past or present should pass validation")
    void testPastOrPresentTimestampPassesValidation() {
        EventRequestDto eventRequestDto = new EventRequestDto(
                "screen_viewed",
                "user123",
                "session456",
                LocalDateTime.now().minusHours(1), // Past timestamp
                Collections.emptyMap()
        );

        Set<ConstraintViolation<EventRequestDto>> violations = validator.validate(eventRequestDto);
        
        assertTrue(violations.isEmpty(), "EventRequestDto with past timestamp should have no validation errors");

        // Test with current time as well
        eventRequestDto = new EventRequestDto(
                "screen_viewed",
                "user123",
                "session456",
                LocalDateTime.now(), // Current timestamp
                Collections.emptyMap()
        );

        violations = validator.validate(eventRequestDto);
        assertTrue(violations.isEmpty(), "EventRequestDto with current timestamp should have no validation errors");
    }

    @Test
    @DisplayName("Properties can be null or empty")
    void testPropertiesCanBeNullOrEmpty() {
        // Test with null properties
        EventRequestDto eventRequestDto = new EventRequestDto(
                "screen_viewed",
                "user123",
                "session456",
                LocalDateTime.now(),
                null
        );

        Set<ConstraintViolation<EventRequestDto>> violations = validator.validate(eventRequestDto);
        assertTrue(violations.isEmpty(), "EventRequestDto with null properties should have no validation errors");

        // Test with empty properties
        eventRequestDto = new EventRequestDto(
                "screen_viewed",
                "user123",
                "session456",
                LocalDateTime.now(),
                Collections.emptyMap()
        );

        violations = validator.validate(eventRequestDto);
        assertTrue(violations.isEmpty(), "EventRequestDto with empty properties should have no validation errors");
    }

    @Test
    @DisplayName("Complex properties should pass validation")
    void testComplexProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("screen", "menu");
        properties.put("category", "pizza");
        properties.put("price", 599);
        properties.put("item_id", "pizza_pepperoni");

        EventRequestDto eventRequestDto = new EventRequestDto(
                "item_viewed",
                "user789",
                "sess_abc123",
                LocalDateTime.now(),
                properties
        );

        Set<ConstraintViolation<EventRequestDto>> violations = validator.validate(eventRequestDto);
        assertTrue(violations.isEmpty(), "EventRequestDto with complex properties should have no validation errors");
    }

    @Test
    @DisplayName("Record should correctly provide getter methods")
    void testRecordGetters() {
        Map<String, Object> properties = Collections.singletonMap("screen", "menu");
        EventRequestDto eventRequestDto = new EventRequestDto(
                "screen_viewed",
                "user123",
                "session456",
                LocalDateTime.now(),
                properties
        );

        assertEquals("screen_viewed", eventRequestDto.eventType());
        assertEquals("user123", eventRequestDto.userId());
        assertEquals("session456", eventRequestDto.sessionId());
        assertEquals(properties, eventRequestDto.properties());
        assertNotNull(eventRequestDto.timestamp());
    }
}