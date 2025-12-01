package com.foodtracker.dashboard.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodtracker.dashboard.dto.DashboardMetricsResponseDto;
import com.foodtracker.dashboard.service.DashboardMetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardMetricsWebSocketHandlerTest {

    @Mock
    private DashboardMetricsService dashboardMetricsService;

    @Mock
    private WebSocketSession session;

    private ObjectMapper objectMapper;

    private DashboardMetricsWebSocketHandler webSocketHandler;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        webSocketHandler = new DashboardMetricsWebSocketHandler(dashboardMetricsService, objectMapper);
    }

    @Test
    void testAfterConnectionEstablished_addsSessionToSet() throws Exception {
        // Arrange
        when(session.getId()).thenReturn("test-session-1");
        when(session.isOpen()).thenReturn(true);

        // Act
        webSocketHandler.afterConnectionEstablished(session);

        // Assert
        assertEquals(1, webSocketHandler.getConnectedClientCount());

        // Verify that initial metrics were sent
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }

    @Test
    void testAfterConnectionEstablished_incrementConnectionCounter() throws Exception {
        // Arrange
        when(session.getId()).thenReturn("test-session-1");
        when(session.isOpen()).thenReturn(true);

        // Act
        webSocketHandler.afterConnectionEstablished(session);

        // Assert
        assertEquals(1, webSocketHandler.getConnectedClientCount());

        // Add another session
        WebSocketSession session2 = mock(WebSocketSession.class);
        when(session2.getId()).thenReturn("test-session-2");
        when(session2.isOpen()).thenReturn(true);

        webSocketHandler.afterConnectionEstablished(session2);
        assertEquals(2, webSocketHandler.getConnectedClientCount());
    }

    @Test
    void testHandleMessage_withRefreshCommand() throws Exception {
        // Arrange
        when(session.getId()).thenReturn("test-session-1");
        when(session.isOpen()).thenReturn(true);
        TextMessage refreshMessage = new TextMessage("refresh");

        DashboardMetricsResponseDto mockMetrics = DashboardMetricsResponseDto.builder()
                .dau(100L)
                .conversionRate(25.0)
                .popularItems(Map.of("pizza", 50L, "burger", 30L))
                .build();
        when(dashboardMetricsService.getDashboardMetrics()).thenReturn(mockMetrics);

        // Act
        webSocketHandler.handleMessage(session, refreshMessage);

        // Assert
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }

    @Test
    void testHandleMessage_withNonRefreshStringCommand() throws Exception {
        // Arrange
        when(session.getId()).thenReturn("test-session-1");
        TextMessage nonRefreshMessage = new TextMessage("not-refresh");

        // Act
        webSocketHandler.handleMessage(session, nonRefreshMessage);

        // Assert
        verify(session, never()).sendMessage(any(TextMessage.class));
    }

    @Test
    void testHandleMessage_withNonStringMessage() throws Exception {
        // Arrange
        when(session.getId()).thenReturn("test-session-1");
        BinaryMessage binaryMessage = new BinaryMessage("test".getBytes());

        // Act
        webSocketHandler.handleMessage(session, binaryMessage);

        // Assert
        verify(session, never()).sendMessage(any(TextMessage.class));
    }

    @Test
    void testHandleTransportError_removesSession() throws Exception {
        // Arrange
        when(session.getId()).thenReturn("test-session-1");
        when(session.isOpen()).thenReturn(true);
        RuntimeException mockException = new RuntimeException("Test exception");

        // Add session first
        webSocketHandler.afterConnectionEstablished(session);
        assertEquals(1, webSocketHandler.getConnectedClientCount());

        // Act
        webSocketHandler.handleTransportError(session, mockException);

        // Assert
        assertEquals(0, webSocketHandler.getConnectedClientCount());
    }

    @Test
    void testAfterConnectionClosed_removesSession() throws Exception {
        // Arrange
        when(session.getId()).thenReturn("test-session-1");
        when(session.isOpen()).thenReturn(true);
        CloseStatus closeStatus = CloseStatus.NORMAL;

        // Add session first
        webSocketHandler.afterConnectionEstablished(session);
        assertEquals(1, webSocketHandler.getConnectedClientCount());

        // Act
        webSocketHandler.afterConnectionClosed(session, closeStatus);

        // Assert
        assertEquals(0, webSocketHandler.getConnectedClientCount());
    }

    @Test
    void testAfterConnectionClosed_decrementsConnectionCounter() throws Exception {
        // Arrange
        when(session.getId()).thenReturn("test-session-1");
        when(session.isOpen()).thenReturn(true);
        CloseStatus closeStatus = CloseStatus.NORMAL;

        // Add two sessions
        WebSocketSession session2 = mock(WebSocketSession.class);
        when(session2.getId()).thenReturn("test-session-2");
        when(session2.isOpen()).thenReturn(true);

        webSocketHandler.afterConnectionEstablished(session);
        webSocketHandler.afterConnectionEstablished(session2);
        assertEquals(2, webSocketHandler.getConnectedClientCount());

        // Act - close one session
        webSocketHandler.afterConnectionClosed(session, closeStatus);

        // Assert
        assertEquals(1, webSocketHandler.getConnectedClientCount());

        // Close second session
        webSocketHandler.afterConnectionClosed(session2, closeStatus);
        assertEquals(0, webSocketHandler.getConnectedClientCount());
    }

    @Test
    void testSupportsPartialMessages_returnsFalse() {
        // Act & Assert
        assertFalse(webSocketHandler.supportsPartialMessages());
    }

    @Test
    void testBroadcastMetrics_sendsMessageToAllSessions() throws Exception {
        // Arrange
        when(session.getId()).thenReturn("test-session-1");
        when(session.isOpen()).thenReturn(true);

        WebSocketSession session2 = mock(WebSocketSession.class);
        when(session2.getId()).thenReturn("test-session-2");
        when(session2.isOpen()).thenReturn(true);

        DashboardMetricsResponseDto metrics = DashboardMetricsResponseDto.builder()
                .dau(100L)
                .conversionRate(25.0)
                .popularItems(Map.of("pizza", 50L, "burger", 30L))
                .build();

        // Add two sessions
        webSocketHandler.afterConnectionEstablished(session);
        webSocketHandler.afterConnectionEstablished(session2);

        // Act
        webSocketHandler.broadcastMetrics(metrics);

        // Assert
        verify(session, times(2)).sendMessage(any(TextMessage.class));
        verify(session2, times(2)).sendMessage(any(TextMessage.class));
    }

    @Test
    void testBroadcastMetrics_withClosedSession_skipsSendMessage() throws Exception {
        // Arrange
        when(session.isOpen()).thenReturn(false); // Closed session

        WebSocketSession session2 = mock(WebSocketSession.class);
        when(session2.getId()).thenReturn("test-session-2");
        when(session2.isOpen()).thenReturn(true);

        DashboardMetricsResponseDto metrics = DashboardMetricsResponseDto.builder()
                .dau(100L)
                .conversionRate(25.0)
                .popularItems(Map.of("pizza", 50L, "burger", 30L))
                .build();

        // Add two sessions
        webSocketHandler.afterConnectionEstablished(session);
        webSocketHandler.afterConnectionEstablished(session2);

        // Act
        webSocketHandler.broadcastMetrics(metrics);

        // Assert
        verify(session, never()).sendMessage(any(TextMessage.class));
        verify(session2, times(2)).sendMessage(any(TextMessage.class));
    }

    @Test
    void testBroadcastMetrics_withIOException_removesSession() throws Exception {
        // Arrange
        when(session.getId()).thenReturn("test-session-1");
        when(session.isOpen()).thenReturn(true);
        doThrow(new IOException("Connection error")).when(session).sendMessage(any(TextMessage.class));

        WebSocketSession session2 = mock(WebSocketSession.class);
        when(session2.getId()).thenReturn("test-session-2");
        when(session2.isOpen()).thenReturn(true);

        DashboardMetricsResponseDto metrics = DashboardMetricsResponseDto.builder()
                .dau(100L)
                .conversionRate(25.0)
                .popularItems(Map.of("pizza", 50L, "burger", 30L))
                .build();

        // Add two sessions
        webSocketHandler.afterConnectionEstablished(session);
        webSocketHandler.afterConnectionEstablished(session2);

        // Act
        webSocketHandler.broadcastMetrics(metrics);

        // Assert - session with IOException should be removed
        assertEquals(1, webSocketHandler.getConnectedClientCount());
        verify(session2, times(2)).sendMessage(any(TextMessage.class)); // Only session2 should receive message
    }

    @Test
    void testGetConnectedClientCount_returnsCorrectCount() throws Exception {
        // Arrange
        when(session.getId()).thenReturn("test-session-1");
        when(session.isOpen()).thenReturn(true);

        WebSocketSession session2 = mock(WebSocketSession.class);
        when(session2.getId()).thenReturn("test-session-2");
        when(session2.isOpen()).thenReturn(true);

        // Act & Assert
        assertEquals(0, webSocketHandler.getConnectedClientCount());

        webSocketHandler.afterConnectionEstablished(session);
        assertEquals(1, webSocketHandler.getConnectedClientCount());

        webSocketHandler.afterConnectionEstablished(session2);
        assertEquals(2, webSocketHandler.getConnectedClientCount());

        webSocketHandler.afterConnectionClosed(session, CloseStatus.NORMAL);
        assertEquals(1, webSocketHandler.getConnectedClientCount());

        webSocketHandler.afterConnectionClosed(session2, CloseStatus.NORMAL);
        assertEquals(0, webSocketHandler.getConnectedClientCount());
    }
}