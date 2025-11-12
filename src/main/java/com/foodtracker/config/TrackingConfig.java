package com.foodtracker.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "tracking")
    public class TrackingConfig {

    /**
     * Base URL for the tracking API endpoint
     */
    private String apiBaseUrl = "http://localhost:8080x/api/track";
}