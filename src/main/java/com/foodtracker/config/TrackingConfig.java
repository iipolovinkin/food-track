package com.foodtracker.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@Component
@Validated
@ConfigurationProperties(prefix = "tracking")
public class TrackingConfig {

    /**
     * Base URL for the tracking API endpoint
     */
    @NotBlank
    @URL(message = "tracking.apiBaseUrl must be a valid URL")
    private String apiBaseUrl = "http://localhost:8080";
}