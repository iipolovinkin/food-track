package com.foodtracker.analytics.service;

import com.foodtracker.api.analytics.ConversionFunnelResponse;
import com.foodtracker.shared.model.Event;
import com.foodtracker.shared.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsServiceImpl implements AnalyticsService {

    private final EventRepository eventRepository;

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
}