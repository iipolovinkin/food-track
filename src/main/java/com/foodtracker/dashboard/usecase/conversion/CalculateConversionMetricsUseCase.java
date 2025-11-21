package com.foodtracker.dashboard.usecase.conversion;

import com.foodtracker.dashboard.dto.ConversionMetricsDto;

public interface CalculateConversionMetricsUseCase {
    ConversionMetricsDto calculateConversionMetrics(String category);
}