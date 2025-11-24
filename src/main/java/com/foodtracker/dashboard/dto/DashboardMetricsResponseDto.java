package com.foodtracker.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardMetricsResponseDto {
    private LocalDateTime timestamp;
    private Long dau; // Daily Active Users
    private Double conversionRate; // Conversion rate percentage
    private Map<String, Long> popularItems; // Popular items data
    private Map<String, Object> categoryMetrics; // Category-specific metrics
}