package com.foodtracker.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "analytics")
public class AnalyticsConfig {

    /**
     * Base URL for the analytics API endpoint
     */
    private String apiBaseUrl = "http://localhost:8080/api";
}