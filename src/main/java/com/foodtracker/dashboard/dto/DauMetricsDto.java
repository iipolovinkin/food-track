package com.foodtracker.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class DauMetricsDto {
    private Long dauCount;
    private LocalDateTime timestamp;
    private String period; // e.g., "last_10_seconds", "last_minute", "last_hour"
}