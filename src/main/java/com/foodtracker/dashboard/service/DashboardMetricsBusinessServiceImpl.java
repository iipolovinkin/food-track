package com.foodtracker.dashboard.service;

import com.foodtracker.dashboard.dto.*;
import com.foodtracker.dashboard.usecase.conversion.CalculateConversionMetricsUseCase;
import com.foodtracker.dashboard.usecase.dashboard.CalculateDashboardMetricsUseCase;
import com.foodtracker.dashboard.usecase.dau.CalculateDauMetricsUseCase;
import com.foodtracker.dashboard.usecase.popular.CalculatePopularItemsMetricsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardMetricsBusinessServiceImpl implements DashboardMetricsBusinessService {

    private final CalculateDashboardMetricsUseCase calculateDashboardMetricsUseCase;
    private final CalculateDauMetricsUseCase calculateDauMetricsUseCase;
    private final CalculateConversionMetricsUseCase calculateConversionMetricsUseCase;
    private final CalculatePopularItemsMetricsUseCase calculatePopularItemsMetricsUseCase;

    @Override
    public DashboardMetricsResponseDto getDashboardMetrics() {
        return calculateDashboardMetricsUseCase.calculateDashboardMetrics();
    }

    @Override
    public DauMetricsDto getDauMetrics() {
        return calculateDauMetricsUseCase.calculateDauMetrics();
    }

    @Override
    public ConversionMetricsDto getConversionMetrics(String category) {
        return calculateConversionMetricsUseCase.calculateConversionMetrics(category);
    }

    @Override
    public PopularItemsMetricsDto getPopularItemsMetrics(String category) {
        return calculatePopularItemsMetricsUseCase.calculatePopularItemsMetrics(category);
    }
}