package com.foodtracker.dashboard.usecase.popular;

import com.foodtracker.dashboard.dto.PopularItemsMetricsDto;

public interface CalculatePopularItemsMetricsUseCase {
    PopularItemsMetricsDto calculatePopularItemsMetrics(String category);
}