package com.foodtracker.dashboard.service;

import com.foodtracker.dashboard.dto.*;

public interface DashboardMetricsBusinessService {
    DashboardMetricsResponseDto getDashboardMetrics();
    DauMetricsDto getDauMetrics();
    ConversionMetricsDto getConversionMetrics(String category);
    PopularItemsMetricsDto getPopularItemsMetrics(String category);
}