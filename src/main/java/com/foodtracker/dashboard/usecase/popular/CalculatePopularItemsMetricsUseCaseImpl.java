package com.foodtracker.dashboard.usecase.popular;

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
public class CalculatePopularItemsMetricsUseCaseImpl implements CalculatePopularItemsMetricsUseCase {

    private final EventRepository eventRepository;

    @Override
    public PopularItemsMetricsDto calculatePopularItemsMetrics(String category) {
        Instant oneHourAgo = Instant.now().minus(Duration.ofHours(1));

        List<Event> events;
        if (category != null && !category.isEmpty()) {
            events = eventRepository.findByEventTypeAndCategory("item_viewed", category, oneHourAgo);
        } else {
            events = eventRepository.findByEventTypeSince("item_viewed", oneHourAgo);
        }

        // Group by item name and count occurrences
        Map<String, Long> itemViewCounts = events.stream()
                .map(event -> getPropertyName(event, "item_name"))
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        item -> item,
                        Collectors.counting()
                ));

        // Sort by view count descending
        List<PopularItemDto> popularItems = itemViewCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10) // Top 10 popular items
                .map(entry -> {
                    String itemName = entry.getKey();
                    Integer viewCount = Math.toIntExact(entry.getValue());

                    // For simplicity, using view count as popularity score for now
                    // We could add more sophisticated calculation here
                    return PopularItemDto.builder()
                            .itemName(itemName)
                            .viewCount(viewCount)
                            .popularityScore((double) viewCount)
                            .build();
                })
                .collect(Collectors.toList());

        return PopularItemsMetricsDto.builder()
                .popularItems(popularItems)
                .timestamp(LocalDateTime.now())
                .category(category)
                .build();
    }

    private String getPropertyName(Event event, String propertyName) {
        if (event.getProperties() != null && event.getProperties().containsKey(propertyName)) {
            return event.getProperties().get(propertyName).toString();
        }
        return null;
    }
}