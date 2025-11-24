package com.foodtracker.dashboard.service;

import com.foodtracker.dashboard.cache.CacheService;
import com.foodtracker.dashboard.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardMetricsServiceImpl implements DashboardMetricsService {

    private final DashboardMetricsBusinessService businessService;
    private final CacheService cacheService;

    private static final String DASHBOARD_METRICS_KEY = "dashboard:metrics";
    private static final String DAU_METRICS_KEY = "dashboard:dau";
    private static final String CONVERSION_METRICS_KEY = "dashboard:conversion";
    private static final String POPULAR_ITEMS_METRICS_KEY = "dashboard:popular_items";
    private static final int CACHE_EXPIRY_SECONDS = 30;

    @Override
    public DashboardMetricsResponseDto getDashboardMetrics() {
        String key = DASHBOARD_METRICS_KEY;
        var cachedMetrics = cacheService.get(key, DashboardMetricsResponseDto.class);

        if (cachedMetrics.isEmpty()) {
            log.info("Cache miss for dashboard metrics, recalculating...");
            var metrics = businessService.getDashboardMetrics();
            cacheService.put(key, metrics, CACHE_EXPIRY_SECONDS);
            return metrics;
        } else {
            log.info("Cache hit for dashboard metrics");
            return cachedMetrics.get();
        }
    }

    @Override
    public DauMetricsDto getDauMetrics() {
        String key = DAU_METRICS_KEY;
        var cachedMetrics = cacheService.get(key, DauMetricsDto.class);

        if (cachedMetrics.isEmpty()) {
            log.info("Cache miss for DAU metrics, recalculating...");
            var metrics = businessService.getDauMetrics();
            cacheService.put(key, metrics, CACHE_EXPIRY_SECONDS);
            return metrics;
        } else {
            log.info("Cache hit for DAU metrics");
            return cachedMetrics.get();
        }
    }

    @Override
    public ConversionMetricsDto getConversionMetrics(String category) {
        String key = CONVERSION_METRICS_KEY + ":" + (category != null ? category : "all");
        var cachedMetrics = cacheService.get(key, ConversionMetricsDto.class);

        if (cachedMetrics.isEmpty()) {
            log.info("Cache miss for conversion metrics, recalculating for category: {}", category);
            var metrics = businessService.getConversionMetrics(category);
            cacheService.put(key, metrics, CACHE_EXPIRY_SECONDS);
            return metrics;
        } else {
            log.info("Cache hit for conversion metrics");
            return cachedMetrics.get();
        }
    }

    @Override
    public PopularItemsMetricsDto getPopularItemsMetrics(String category) {
        String key = POPULAR_ITEMS_METRICS_KEY + ":" + (category != null ? category : "all");
        var cachedMetrics = cacheService.get(key, PopularItemsMetricsDto.class);

        if (cachedMetrics.isEmpty()) {
            log.info("Cache miss for popular items metrics, recalculating for category: {}", category);
            var metrics = businessService.getPopularItemsMetrics(category);
            cacheService.put(key, metrics, CACHE_EXPIRY_SECONDS);
            return metrics;
        } else {
            log.info("Cache hit for popular items metrics");
            return cachedMetrics.get();
        }
    }
}