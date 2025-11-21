package com.foodtracker.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConversionMetricsDto {
    private Double conversionRate;
    private String category; // pizza, burger, etc.
    private Integer totalSessions;
    private Integer conversions;
    private LocalDateTime timestamp;
    private List<ConversionStepDto> conversionSteps; // Detailed step-by-step conversion
}