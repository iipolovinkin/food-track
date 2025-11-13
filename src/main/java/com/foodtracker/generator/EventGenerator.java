package com.foodtracker.generator;

import com.foodtracker.generator.config.GeneratorConfig;
import com.foodtracker.api.tracking.TrackingEventRequestDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class EventGenerator {
    
    private final Random random = new Random();
    private final GeneratorConfig config;
    
    // Pizza items
    private final List<String> pizzaItems = Arrays.asList(
        "pizza_margarita", "pizza_pepperoni", "pizza_vegetarian", "pizza_hawaiian", "pizza_meat_lovers",
        "pizza_supreme", "pizza_bbq_chicken", "pizza_four_cheese", "pizza_mushroom", "pizza_spicy"
    );
    
    // Burger items
    private final List<String> burgerItems = Arrays.asList(
        "burger_classic", "burger_cheese", "burger_deluxe", "burger_bbq", "burger_veggie",
        "burger_chicken", "burger_fish", "burger_mushroom", "burger_spicy", "burger_double"
    );
    
    // Platforms for app_opened event
    private final List<String> platforms = Arrays.asList("android", "ios");
    
    // Payment methods for order_placed event
    private final List<String> paymentMethods = Arrays.asList("card", "cash", "digital_wallet");
    
    // Error codes for payment_failed event
    private final List<String> errorCodes = Arrays.asList("card_declined", "insufficient_funds", "network_error", "invalid_card");
    
    public EventGenerator(GeneratorConfig config) {
        this.config = config;
    }
    
    public TrackingEventRequestDto generateEvent(String eventType, String userId, String sessionId, LocalDateTime timestamp, String category) {
        Map<String, Object> properties = new HashMap<>();
        
        switch (eventType) {
            case "app_opened":
                properties.put("platform", getRandomItem(platforms));
                properties.put("version", "1.2." + random.nextInt(5));
                break;
                
            case "screen_viewed":
                properties.put("screen", getRandomScreen());
                properties.put("category", category);
                break;
                
            case "item_viewed":
                String itemId = category.equals("pizza") ? 
                    getRandomItem(pizzaItems) : getRandomItem(burgerItems);
                properties.put("item_id", itemId);
                properties.put("category", category);
                properties.put("price", random.nextInt(1500) + 300); // Price between 300-1800
                break;
                
            case "item_added_to_cart":
                String addedItemId = category.equals("pizza") ? 
                    getRandomItem(pizzaItems) : getRandomItem(burgerItems);
                properties.put("item_id", addedItemId);
                properties.put("category", category);
                properties.put("quantity", random.nextInt(3) + 1);
                properties.put("price", random.nextInt(1500) + 300);
                break;
                
            case "checkout_started":
                properties.put("cart_total", random.nextInt(3000) + 500); // Total between 500-3500
                properties.put("items_count", random.nextInt(10) + 1);
                break;
                
            case "order_placed":
                properties.put("order_id", "ord_" + System.currentTimeMillis() + "_" + random.nextInt(1000));
                properties.put("total", random.nextInt(3000) + 500);
                properties.put("payment_method", getRandomItem(paymentMethods));
                properties.put("category", category);
                break;
                
            case "payment_failed":
                properties.put("error_code", getRandomItem(errorCodes));
                properties.put("order_id", "ord_" + System.currentTimeMillis() + "_" + random.nextInt(1000));
                properties.put("total", random.nextInt(3000) + 500);
                properties.put("category", category);
                break;
                
            default:
                properties.put("category", category);
                break;
        }
        
        return new TrackingEventRequestDto(
            eventType,
            userId,
            sessionId,
            timestamp,
            properties
        );
    }
    
    private String getRandomScreen() {
        List<String> screens = Arrays.asList("menu", "category", "item_detail", "cart", "checkout", "order_confirmation");
        return getRandomItem(screens);
    }
    
    private <T> T getRandomItem(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }
    
    public LocalDateTime addRandomTime(LocalDateTime baseTime, int minMinutes, int maxMinutes) {
        int minutesToAdd = random.nextInt((maxMinutes - minMinutes) + 1) + minMinutes;
        return baseTime.plusMinutes(minutesToAdd);
    }
    
    public LocalDateTime addRandomSeconds(LocalDateTime baseTime, int minSeconds, int maxSeconds) {
        int secondsToAdd = random.nextInt((maxSeconds - minSeconds) + 1) + minSeconds;
        return baseTime.plusSeconds(secondsToAdd);
    }
    
    public String getRandomCategory() {
        return random.nextDouble() < config.getPizzaRatio() ? "pizza" : "burger";
    }
}