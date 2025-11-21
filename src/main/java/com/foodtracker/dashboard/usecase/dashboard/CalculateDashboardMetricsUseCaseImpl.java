package com.foodtracker.dashboard.usecase.dashboard;

import com.foodtracker.dashboard.dto.*;
import com.foodtracker.shared.repository.Event;
import com.foodtracker.shared.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalculateDashboardMetricsUseCaseImpl implements CalculateDashboardMetricsUseCase {

    private final EventRepository eventRepository;

    @Override
    public DashboardMetricsResponseDto calculateDashboardMetrics() {
        Instant oneHourAgo = getOneHourAgo();

        // Calculate DAU (users who had events in the last hour)
        Long dau = eventRepository.countDistinctUsersSince(oneHourAgo);

        // Calculate conversion rate
        Double conversionRate = calculateConversionRate();

        // Get popular items
        Map<String, Long> popularItems = getPopularItems("all");

        // Get category metrics
        Map<String, Object> categoryMetrics = getCategoryMetrics();

        return DashboardMetricsResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .dau(dau)
                .conversionRate(conversionRate)
                .popularItems(popularItems)
                .categoryMetrics(categoryMetrics)
                .build();
    }

    private Instant getOneHourAgo() {
        return Instant.now().minus(Duration.ofHours(1));
    }

    private Double calculateConversionRate() {
        Instant oneHourAgo = getOneHourAgo();
        long sessions = eventRepository.countByEventTypeSince("app_opened", oneHourAgo);
        long orders = eventRepository.countByEventTypeSince("order_placed", oneHourAgo);

        return sessions > 0 ? (double) orders / sessions * 100 : 0.0;
    }

    private Map<String, Long> getPopularItems(String category) {
        Instant oneHourAgo = getOneHourAgo();

        List<Event> events;
        if ("all".equals(category) || category == null) {
            events = eventRepository.findByEventTypeSince("item_viewed", oneHourAgo);
        } else {
            events = eventRepository.findByEventTypeAndCategory("item_viewed", category, oneHourAgo);
        }

        Map<String, Long> popularItems = events.stream()
                .map(event -> getPropertyName(event, "item_name"))
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        item -> item,
                        Collectors.counting()
                ));

        return new HashMap<>(popularItems);
    }

    private Map<String, Object> getCategoryMetrics() {
        Instant oneHourAgo = getOneHourAgo();

        Map<String, Object> metrics = new HashMap<>();

        // Pizza metrics
        metrics.put("pizza", Map.of(
            "dau", eventRepository.countDistinctUsersByCategorySince("pizza", oneHourAgo),
            "conversions", eventRepository.countOrdersByCategorySince("pizza", oneHourAgo),
            "conversion_rate", calculateConversionRateForCategory("pizza")
        ));

        // Burger metrics
        metrics.put("burger", Map.of(
            "dau", eventRepository.countDistinctUsersByCategorySince("burger", oneHourAgo),
            "conversions", eventRepository.countOrdersByCategorySince("burger", oneHourAgo),
            "conversion_rate", calculateConversionRateForCategory("burger")
        ));

        return metrics;
    }

    private String getPropertyName(Event event, String propertyName) {
        if (event.getProperties() != null && event.getProperties().containsKey(propertyName)) {
            return event.getProperties().get(propertyName).toString();
        }
        return null;
    }

    private Double calculateConversionRateForCategory(String category) {
        Instant oneHourAgo = getOneHourAgo();
        long sessions = eventRepository.countByEventTypeAndCategory("app_opened", category, oneHourAgo);
        long orders = eventRepository.countByEventTypeAndCategory("order_placed", category, oneHourAgo);

        return sessions > 0 ? (double) orders / sessions * 100 : 0.0;
    }
}