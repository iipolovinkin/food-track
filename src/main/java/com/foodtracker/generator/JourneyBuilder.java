package com.foodtracker.generator;

import com.foodtracker.generator.config.GeneratorConfig;
import com.foodtracker.generator.gateway.tracking.TrackingEventRequestDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class JourneyBuilder {

    private final EventGenerator eventGenerator;
    private final GeneratorConfig config;

    public JourneyBuilder(EventGenerator eventGenerator, GeneratorConfig config) {
        this.eventGenerator = eventGenerator;
        this.config = config;
    }

    public List<TrackingEventRequestDto> buildCompleteJourney(String userId, String sessionId, LocalDateTime startTime, String category) {
        List<TrackingEventRequestDto> events = new ArrayList<>();

        // App opened
        LocalDateTime currentTime = startTime;
        events.add(eventGenerator.generateEvent("app_opened", userId, sessionId, currentTime, category));

        // Screen viewed
        currentTime = eventGenerator.addRandomSeconds(currentTime, 5, 30);
        events.add(eventGenerator.generateEvent("screen_viewed", userId, sessionId, currentTime, category));

        // Item viewed
        currentTime = eventGenerator.addRandomSeconds(currentTime, 10, 60);
        events.add(eventGenerator.generateEvent("item_viewed", userId, sessionId, currentTime, category));

        // Additional item views
        if (Math.random() < 0.6) { // 60% chance of viewing another item
            currentTime = eventGenerator.addRandomSeconds(currentTime, 10, 60);
            events.add(eventGenerator.generateEvent("item_viewed", userId, sessionId, currentTime, category));
        }

        // Item added to cart
        currentTime = eventGenerator.addRandomSeconds(currentTime, 5, 30);
        events.add(eventGenerator.generateEvent("item_added_to_cart", userId, sessionId, currentTime, category));

        // Another item added to cart (might happen)
        if (Math.random() < 0.4) { // 40% chance of adding another item
            currentTime = eventGenerator.addRandomSeconds(currentTime, 5, 30);
            events.add(eventGenerator.generateEvent("item_added_to_cart", userId, sessionId, currentTime, category));
        }

        // Screen viewed (cart or checkout)
        currentTime = eventGenerator.addRandomSeconds(currentTime, 5, 30);
        events.add(eventGenerator.generateEvent("screen_viewed", userId, sessionId, currentTime, "cart"));

        // Checkout started
        currentTime = eventGenerator.addRandomSeconds(currentTime, 10, 60);
        events.add(eventGenerator.generateEvent("checkout_started", userId, sessionId, currentTime, category));

        // Determine if order is placed or payment fails
        if (Math.random() < (1 - config.getPaymentFailedProbability())) {
            // Order placed successfully
            currentTime = eventGenerator.addRandomSeconds(currentTime, 30, 120);
            events.add(eventGenerator.generateEvent("order_placed", userId, sessionId, currentTime, category));
        } else {
            // Payment failed
            currentTime = eventGenerator.addRandomSeconds(currentTime, 30, 120);
            events.add(eventGenerator.generateEvent("payment_failed", userId, sessionId, currentTime, category));

            // Maybe retry after payment failure
            if (Math.random() < 0.3) { // 30% chance of retrying
                currentTime = eventGenerator.addRandomSeconds(currentTime, 60, 300);
                events.add(eventGenerator.generateEvent("checkout_started", userId, sessionId, currentTime, category));

                currentTime = eventGenerator.addRandomSeconds(currentTime, 30, 120);
                events.add(eventGenerator.generateEvent("order_placed", userId, sessionId, currentTime, category));
            }
        }

        return events;
    }

    public List<TrackingEventRequestDto> buildPartialJourney(String userId, String sessionId, LocalDateTime startTime, String category, int maxStage) {
        List<TrackingEventRequestDto> events = new ArrayList<>();

        LocalDateTime currentTime = startTime;

        // Stage 1: App opened
        events.add(eventGenerator.generateEvent("app_opened", userId, sessionId, currentTime, category));
        currentTime = eventGenerator.addRandomSeconds(currentTime, 5, 30);

        // Stage 2: Screen viewed
        if (maxStage >= 2) {
            events.add(eventGenerator.generateEvent("screen_viewed", userId, sessionId, currentTime, category));
            currentTime = eventGenerator.addRandomSeconds(currentTime, 10, 60);
        }

        // Stage 3: Item viewed
        if (maxStage >= 3) {
            events.add(eventGenerator.generateEvent("item_viewed", userId, sessionId, currentTime, category));
            currentTime = eventGenerator.addRandomSeconds(currentTime, 10, 60);
        }

        // Stage 4: Item added to cart
        if (maxStage >= 4) {
            events.add(eventGenerator.generateEvent("item_added_to_cart", userId, sessionId, currentTime, category));
            currentTime = eventGenerator.addRandomSeconds(currentTime, 5, 30);
        }

        // Stage 5: Checkout started
        if (maxStage >= 5) {
            events.add(eventGenerator.generateEvent("screen_viewed", userId, sessionId, currentTime, "cart"));
            currentTime = eventGenerator.addRandomSeconds(currentTime, 5, 30);
            events.add(eventGenerator.generateEvent("checkout_started", userId, sessionId, currentTime, category));
            currentTime = eventGenerator.addRandomSeconds(currentTime, 30, 120);
        }

        // Stage 6: Order placed or payment failed
        if (maxStage >= 6) {
            if (Math.random() < (1 - config.getPaymentFailedProbability())) {
                events.add(eventGenerator.generateEvent("order_placed", userId, sessionId, currentTime, category));
            } else {
                events.add(eventGenerator.generateEvent("payment_failed", userId, sessionId, currentTime, category));
            }
        }

        return events;
    }
}