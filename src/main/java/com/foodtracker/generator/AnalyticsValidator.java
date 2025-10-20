package com.foodtracker.generator;

import com.foodtracker.dto.analytics.ConversionFunnelResponse;
import com.foodtracker.model.Event;
import com.foodtracker.service.EventService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AnalyticsValidator {

    private final EventService eventService;

    public AnalyticsValidator(EventService eventService) {
        this.eventService = eventService;
    }

    public void validateDAU(String eventType, LocalDateTime date) {
        long dauCount = eventService.getDistinctUserCountByEventTypeAndDate(eventType, date);
        System.out.println("DAU for " + eventType + " on " + date.toLocalDate() + ": " + dauCount);
    }

    public void validateConversionFunnel(String category, LocalDateTime start, LocalDateTime end) {
        ConversionFunnelResponse response = eventService.getConversionFunnelAnalytics(category, start, end);
        System.out.println("Conversion Funnel for " + category + ":");
        System.out.println("  Viewed: " + response.getViewedCount());
        System.out.println("  Added: " + response.getAddedCount());
        System.out.println("  Ordered: " + response.getOrderedCount());
        System.out.println("  Conversion Rate: " + response.getConversionRate() + "%");
    }

    public void validatePopularItems() {
        // Get all item_viewed events to identify popular items
        List<Event> itemViewedEvents = eventService.getEventsByType("item_viewed");

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
                        System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " views")
                );
    }

    public void validateCartToOrderConversion() {
        List<Event> checkoutStartedEvents = eventService.getEventsByType("checkout_started");
        List<Event> orderPlacedEvents = eventService.getEventsByType("order_placed");

        double conversionRate = checkoutStartedEvents.size() > 0 ?
                (double) orderPlacedEvents.size() / checkoutStartedEvents.size() * 100 : 0;

        System.out.println("Cart-to-Order Conversion Rate: " +
                String.format("%.2f", conversionRate) + "% " +
                "(" + orderPlacedEvents.size() + "/" + checkoutStartedEvents.size() + ")");
    }

    public void runAllValidations(LocalDateTime startDate, LocalDateTime endDate) {
        System.out.println("\n--- Analytics Validation Results ---");

        validateDAU("app_opened", startDate);

        validateConversionFunnel("pizza", startDate, endDate);
        validateConversionFunnel("burger", startDate, endDate);

        System.out.println("Top 5 Popular Items:");
        validatePopularItems();

        validateCartToOrderConversion();

        System.out.println("--- End of Analytics Validation ---\n");
    }
}