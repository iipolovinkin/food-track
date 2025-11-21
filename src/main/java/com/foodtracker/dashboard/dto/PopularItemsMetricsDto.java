package com.foodtracker.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PopularItemsMetricsDto {
    private List<PopularItemDto> popularItems;
    private LocalDateTime timestamp;
    private String category; // pizza, burger, etc.
}