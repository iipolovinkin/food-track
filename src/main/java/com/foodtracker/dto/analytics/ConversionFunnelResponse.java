package com.foodtracker.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversionFunnelResponse {
    private String category;
    private long viewedCount;
    private long addedCount;
    private long orderedCount;
    private double conversionRate;
    private Map<String, Object> additionalMetrics;
}