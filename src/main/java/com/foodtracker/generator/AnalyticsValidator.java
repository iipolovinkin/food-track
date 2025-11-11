package com.foodtracker.generator;

import com.foodtracker.analytics.dto.ConversionFunnelResponse;
import com.foodtracker.analytics.service.AnalyticsService;
import com.foodtracker.shared.model.Event;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class AnalyticsValidator {

    private final AnalyticsService analyticsService;

    public void validateDAU(String eventType, LocalDateTime date) {
        long dauCount = analyticsService.getDistinctUserCountByEventTypeAndDate(eventType, date);
        log.info("DAU for {} on {}: {}", eventType, date.toLocalDate(), dauCount);
    }

    public void validateConversionFunnel(String category, LocalDateTime start, LocalDateTime end) {
        ConversionFunnelResponse response = analyticsService.getConversionFunnelAnalytics(category, start, end);
        log.info("Conversion Funnel for {}:", category);
        log.info("  Viewed: {}", response.getViewedCount());
        log.info("  Added: {}", response.getAddedCount());
        log.info("  Ordered: {}", response.getOrderedCount());
        log.info("  Conversion Rate: {}%", response.getConversionRate());
    }

    public void validatePopularItems() {
        // Get all item_viewed events to identify popular items
        List<Event> itemViewedEvents = getEventsByType("item_viewed");

        // Group by item_id and count occurrences
        java.util.Map<String, Integer> itemCounts = new java.util.HashMap<>();
        for (Event event : itemViewedEvents) {
            if (event.getProperties() != null && event.getProperties().containsKey("item_id")) {
                String itemId = (String) event.getProperties().get("item_id");
                itemCounts.put(itemId, itemCounts.getOrDefault(itemId, 0) + 1);
            }
        }

        // Sort and get top 5 items
        itemCounts.entrySet().stream()
                .sorted(java.util.Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .forEach(entry ->
                        log.info("  {}: {} views", entry.getKey(), entry.getValue())
                );
    }

    public void validateCartToOrderConversion() {
        List<Event> checkoutStartedEvents = getEventsByType("checkout_started");
        List<Event> orderPlacedEvents = getEventsByType("order_placed");

        double conversionRate = !checkoutStartedEvents.isEmpty() ?
                (double) orderPlacedEvents.size() / checkoutStartedEvents.size() * 100 : 0;

        log.info("Cart-to-Order Conversion Rate: {}% ({}/{})",
                String.format("%.2f", conversionRate),
                orderPlacedEvents.size(),
                checkoutStartedEvents.size());
    }

    public void runAllValidations(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("\n--- Analytics Validation Results ---");

        validateDAU("app_opened", startDate);

        validateConversionFunnel("pizza", startDate, endDate);
        validateConversionFunnel("burger", startDate, endDate);

        log.info("Top 5 Popular Items:");
        validatePopularItems();

        validateCartToOrderConversion();

        log.info("--- End of Analytics Validation ---\n");
    }

    private List<Event> getEventsByType(String eventType) {
        return analyticsService.getEventsByType(eventType);
    }
}