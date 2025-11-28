package com.foodtracker.dashboard.config;

import com.foodtracker.dashboard.handler.DashboardMetricsWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final DashboardMetricsWebSocketHandler dashboardMetricsWebSocketHandler;

    public WebSocketConfig(DashboardMetricsWebSocketHandler dashboardMetricsWebSocketHandler) {
        this.dashboardMetricsWebSocketHandler = dashboardMetricsWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(dashboardMetricsWebSocketHandler, "/ws/dashboard")
//                .setAllowedOrigins("*"); // In production, configure specific origins
    }
}