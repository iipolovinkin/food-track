package com.foodtracker.dashboard.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodtracker.dashboard.dto.DashboardMetricsResponseDto;
import com.foodtracker.dashboard.service.DashboardMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
@Slf4j
public class DashboardMetricsWebSocketHandler implements WebSocketHandler {

    private final DashboardMetricsService dashboardMetricsService;
    private final ObjectMapper objectMapper;

    private final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private final AtomicInteger connectionCounter = new AtomicInteger(0);

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        int connectionCount = connectionCounter.incrementAndGet();
        log.info("New WebSocket connection established. Session ID: {}, Total connections: {}",
                session.getId(), connectionCount);

        // Send initial metrics data to the newly connected client
        sendInitialMetrics(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        // For now, we only send data to clients, so we don't expect incoming messages
        // But we log for debugging purposes
        log.debug("Received message from session {}: {}", session.getId(), message.getPayload());

        // Optionally, implement response to specific commands from clients
        if (message.getPayload() instanceof String) {
            String payload = (String) message.getPayload();
            if ("refresh".equalsIgnoreCase(payload)) {
                // Send fresh metrics when client requests refresh
                sendInitialMetrics(session);
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("Transport error in WebSocket session {}: {}", session.getId(), exception.getMessage(), exception);
        sessions.remove(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        sessions.remove(session);
        int connectionCount = connectionCounter.decrementAndGet();
        log.info("WebSocket connection closed. Session ID: {}, Close status: {}, Total connections: {}",
                session.getId(), closeStatus, connectionCount);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * Send metrics data to all connected WebSocket clients
     */
    public void broadcastMetrics(DashboardMetricsResponseDto metrics) {
        String metricsJson;
        try {
            metricsJson = objectMapper.writeValueAsString(metrics);
        } catch (Exception e) {
            log.error("Error serializing metrics to JSON", e);
            return;
        }

        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(metricsJson));
                }
            } catch (IOException e) {
                log.error("Error broadcasting metrics to session {}", session.getId(), e);
                // Remove session if there's an error
                sessions.remove(session);
                try {
                    session.close(CloseStatus.SERVER_ERROR);
                } catch (IOException ioException) {
                    log.error("Error closing failed session", ioException);
                }
            }
        }
    }

    /**
     * Send initial metrics data to a newly connected client
     */
    private void sendInitialMetrics(WebSocketSession session) {
        try {
            DashboardMetricsResponseDto metrics = dashboardMetricsService.getDashboardMetrics();
            String metricsJson = objectMapper.writeValueAsString(metrics);
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(metricsJson));
            }
        } catch (Exception e) {
            log.error("Error sending initial metrics to session {}", session.getId(), e);
        }
    }

    /**
     * Get the number of currently connected clients
     */
    public int getConnectedClientCount() {
        return sessions.size();
    }
}