package com.foodtracker.dashboard.service;

import com.foodtracker.dashboard.dto.DashboardMetricsResponseDto;
import com.foodtracker.dashboard.handler.DashboardMetricsWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardMetricsWebSocketService {

    private final DashboardMetricsService dashboardMetricsService;
    private final DashboardMetricsWebSocketHandler webSocketHandler;

    /**
     * Scheduled method to push updated metrics to connected WebSocket clients every 10 seconds
     */
    @Scheduled(fixedRate = 10000) // Update every 10 seconds
    public void pushMetricsUpdates() {
        try {
            // Only push updates if there are connected clients
            if (webSocketHandler.getConnectedClientCount() > 0) {
                DashboardMetricsResponseDto metrics = dashboardMetricsService.getDashboardMetrics();
                webSocketHandler.broadcastMetrics(metrics);
                log.debug("Pushed metrics update to {} connected WebSocket clients", 
                        webSocketHandler.getConnectedClientCount());
            }
        } catch (Exception e) {
            log.error("Error pushing metrics updates to WebSocket clients", e);
        }
    }

    /**
     * Push metrics update immediately to all connected clients
     */
    public void pushImmediateUpdate() {
        try {
            DashboardMetricsResponseDto metrics = dashboardMetricsService.getDashboardMetrics();
            webSocketHandler.broadcastMetrics(metrics);
            log.info("Pushed immediate metrics update to {} connected WebSocket clients", 
                    webSocketHandler.getConnectedClientCount());
        } catch (Exception e) {
            log.error("Error pushing immediate metrics update to WebSocket clients", e);
        }
    }
}