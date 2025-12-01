package com.foodtracker.dashboard.usecase.popular;

import com.foodtracker.dashboard.dto.PopularItemDto;
import com.foodtracker.dashboard.dto.PopularItemsMetricsDto;
import com.foodtracker.shared.repository.Event;
import com.foodtracker.shared.repository.EventRepository;
import com.foodtracker.shared.service.TimeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

class CalculatePopularItemsMetricsUseCaseImplTest {
    @Mock
    EventRepository eventRepository;
    @Mock
    TimeService timeService;
    @InjectMocks
    CalculatePopularItemsMetricsUseCaseImpl sut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCalculatePopularItemsMetrics() {
        String category = "category";
        String itemViewed = "item_viewed";
        Event e1 = Event.builder()
                .id(1L)
                .eventType("itemType1")
                .userId("userId1")
                .sessionId("sessionId1")
                .timestamp(Instant.now())
                .properties(Map.of("item_name", "item_name1"))
                .build();
        List<Event> events = List.of(e1);
        when(eventRepository.findByEventTypeAndCategory(eq(itemViewed), eq(category), any(Instant.class)))
                .thenReturn(events);

        LocalDateTime timestamp = LocalDateTime.of(2025, Month.NOVEMBER, 30, 22, 10, 7);
        when(timeService.getLocalDateTimeNow()).thenReturn(timestamp);

        // Act
        PopularItemsMetricsDto result = sut.calculatePopularItemsMetrics(category);

        // Assert
        List<PopularItemDto> itemName = List.of(new PopularItemDto("item_name1", 1, null, null, 1.0));
        PopularItemsMetricsDto expected = new PopularItemsMetricsDto(
                itemName, timestamp, category);

        Assertions.assertEquals(expected, result);
    }
}

