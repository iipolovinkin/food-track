package com.foodtracker.generator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "generator")
public class GeneratorConfig {

    /**
     * Number of users to generate
     */
    private int userCount = 75;

    /**
     * Date range for events (in days from now)
     */
    private int daysFromNow = 7;

    /**
     * Number of sessions per user (min and max)
     */
    private int minSessionsPerUser = 1;
    private int maxSessionsPerUser = 5;

    /**
     * Probability of different event types (0.0 to 1.0)
     */
    private double appOpenedProbability = 1.0;
    private double screenViewedProbability = 0.9;
    private double itemViewedProbability = 0.8;
    private double itemAddedToCartProbability = 0.6;
    private double checkoutStartedProbability = 0.4;
    private double orderPlacedProbability = 0.3;
    private double paymentFailedProbability = 0.1;

    /**
     * Batch size for API submissions
     */
    private int batchSize = 10;

    /**
     * Enable verbose logging
     */
    private boolean verbose = false;

    /**
     * Category distribution (0.0 to 1.0)
     */
    private double pizzaRatio = 0.5; // 50% pizza, 50% burger
}