package com.foodtracker.dashboard.controller;

import com.foodtracker.dashboard.dto.ConversionMetricsDto;
import com.foodtracker.dashboard.dto.DashboardMetricsResponseDto;
import com.foodtracker.dashboard.dto.DauMetricsDto;
import com.foodtracker.dashboard.dto.PopularItemsMetricsDto;
import com.foodtracker.dashboard.service.DashboardMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard/metrics")
@RequiredArgsConstructor
@Slf4j
public class DashboardMetricsController {

    private final DashboardMetricsService dashboardMetricsService;

    @GetMapping
    public ResponseEntity<DashboardMetricsResponseDto> getDashboardMetrics() {
        log.info("Received request for dashboard metrics");
        DashboardMetricsResponseDto metrics = dashboardMetricsService.getDashboardMetrics();
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/dau")
    public ResponseEntity<DauMetricsDto> getDauMetrics() {
        log.info("Received request for DAU metrics");
        DauMetricsDto dauMetrics = dashboardMetricsService.getDauMetrics();
        return ResponseEntity.ok(dauMetrics);
    }

    @GetMapping("/conversion")
    public ResponseEntity<ConversionMetricsDto> getConversionMetrics(@RequestParam(required = false) String category) {
        log.info("Received request for conversion metrics, category: {}", category);
        ConversionMetricsDto conversionMetrics = dashboardMetricsService.getConversionMetrics(category);
        return ResponseEntity.ok(conversionMetrics);
    }

    @GetMapping("/popular-items")
    public ResponseEntity<PopularItemsMetricsDto> getPopularItemsMetrics(@RequestParam(required = false) String category) {
        log.info("Received request for popular items metrics, category: {}", category);
        PopularItemsMetricsDto popularItemsMetrics = dashboardMetricsService.getPopularItemsMetrics(category);
        return ResponseEntity.ok(popularItemsMetrics);
    }
}