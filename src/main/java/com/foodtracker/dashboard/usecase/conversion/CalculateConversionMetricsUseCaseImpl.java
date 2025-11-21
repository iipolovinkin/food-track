package com.foodtracker.dashboard.usecase.conversion;

import com.foodtracker.dashboard.dto.ConversionMetricsDto;
import com.foodtracker.dashboard.dto.ConversionStepDto;
import com.foodtracker.shared.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalculateConversionMetricsUseCaseImpl implements CalculateConversionMetricsUseCase {

    private final EventRepository eventRepository;

    @Override
    public ConversionMetricsDto calculateConversionMetrics(String category) {
        // Define the conversion steps in order
        List<String> conversionSteps = Arrays.asList(
                "app_opened",
                "screen_viewed",
                "item_viewed",
                "item_added_to_cart",
                "checkout_started",
                "order_placed"
        );

        Instant oneHourAgo = Instant.now().minus(Duration.ofHours(1));
        Map<String, Long> stepCounts = new HashMap<>();

        for (String step : conversionSteps) {
            long count;
            if (category != null && !category.isEmpty()) {
                count = eventRepository.countByEventTypeAndCategory(step, category, oneHourAgo);
            } else {
                count = eventRepository.countByEventTypeSince(step, oneHourAgo);
            }
            stepCounts.put(step, count);
        }

        // Calculate total sessions - users who reached the first step
        long totalSessions = stepCounts.get("app_opened");

        // Calculate overall conversion rate from first to last step
        long ordersPlaced = stepCounts.get("order_placed");
        Double conversionRate = totalSessions > 0 ? (double) ordersPlaced / totalSessions * 100 : 0.0;

        List<ConversionStepDto> conversionStepDtos = conversionSteps.stream()
                .map(step -> ConversionStepDto.builder()
                        .stepName(step)
                        .stepCount(stepCounts.get(step).intValue())
                        .conversionRate(totalSessions > 0 ? (double) stepCounts.get(step) / totalSessions * 100 : 0.0)
                        .build())
                .collect(Collectors.toList());

        return ConversionMetricsDto.builder()
                .conversionRate(conversionRate)
                .category(category)
                .totalSessions(Math.toIntExact(totalSessions))
                .conversions(Math.toIntExact(ordersPlaced))
                .timestamp(LocalDateTime.now())
                .conversionSteps(conversionStepDtos)
                .build();
    }
}