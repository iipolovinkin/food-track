package com.foodtracker.dashboard.cache;

import com.foodtracker.dashboard.service.DashboardMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardCacheRefreshService {

    private final DashboardMetricsService dashboardMetricsService;

    /**
     * Updates the dashboard metrics cache every 10 seconds to provide near real-time metrics
     */
    @Scheduled(fixedRate = 10000)
    public void refreshDashboardMetricsCache() {
        log.debug("Refreshing dashboard metrics cache...");
        try {
            dashboardMetricsService.getDashboardMetrics(); // This will recalculate and cache the metrics
            log.debug("Dashboard metrics cache refreshed successfully");
        } catch (Exception e) {
            log.error("Error refreshing dashboard metrics cache", e);
        }
    }

    /**
     * Updates the DAU metrics cache every 5 seconds for real-time DAU tracking
     */
    @Scheduled(fixedRate = 5000)
    public void refreshDauMetricsCache() {
        log.debug("Refreshing DAU metrics cache...");
        try {
            dashboardMetricsService.getDauMetrics(); // This will recalculate and cache the DAU metrics
            log.debug("DAU metrics cache refreshed successfully");
        } catch (Exception e) {
            log.error("Error refreshing DAU metrics cache", e);
        }
    }

    /**
     * Updates the conversion metrics cache every 15 seconds
     */
    @Scheduled(fixedRate = 15000)
    public void refreshConversionMetricsCache() {
        log.debug("Refreshing conversion metrics cache...");
        try {
            dashboardMetricsService.getConversionMetrics(null); // Refresh general conversion metrics
            dashboardMetricsService.getConversionMetrics("pizza"); // Refresh pizza-specific metrics
            dashboardMetricsService.getConversionMetrics("burger"); // Refresh burger-specific metrics
            log.debug("Conversion metrics cache refreshed successfully");
        } catch (Exception e) {
            log.error("Error refreshing conversion metrics cache", e);
        }
    }

    /**
     * Updates the popular items metrics cache every 20 seconds
     */
    @Scheduled(fixedRate = 20000)
    public void refreshPopularItemsMetricsCache() {
        log.debug("Refreshing popular items metrics cache...");
        try {
            dashboardMetricsService.getPopularItemsMetrics(null); // Refresh general popular items
            dashboardMetricsService.getPopularItemsMetrics("pizza"); // Refresh pizza-specific items
            dashboardMetricsService.getPopularItemsMetrics("burger"); // Refresh burger-specific items
            log.debug("Popular items metrics cache refreshed successfully");
        } catch (Exception e) {
            log.error("Error refreshing popular items metrics cache", e);
        }
    }
}