package com.foodtracker.api.tracking;

import com.foodtracker.config.TrackingConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

/**
 * Implementation of TrackingGateway that uses WebClient for HTTP calls to tracking endpoints.
 * Provides better performance through connection pooling and improved HTTP handling compared to HttpURLConnection.
 * NOTE: Currently using .block() for compatibility with existing synchronous interface.
 * In future, consider migrating to fully reactive approach.
 */
@Component
@Slf4j
public class WebClientTrackingGatewayImpl implements TrackingGateway {
    private final WebClient webClient;

    public WebClientTrackingGatewayImpl(TrackingConfig trackingConfig) {
        this.webClient = WebClient.builder()
                .baseUrl(trackingConfig.getApiBaseUrl())
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10MB max
                .build();
    }

    @Override
    public boolean sendEvent(TrackingEventRequestDto event) {
        try {
            String response = webClient.post()
                    .uri("/api/track")
                    .bodyValue(event)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();

            // Check if the response indicates success
            return response != null && response.contains("Event tracked successfully");
        } catch (WebClientResponseException e) {
            log.error("HTTP error sending event (status {}):", e.getStatusCode(), e);
            return false;
        } catch (Exception e) {
            log.error("Error sending event to API: ", e);
            return false;
        }
    }

    @Override
    public boolean validateEvent(TrackingEventRequestDto event) {
        return event.eventType() != null && !event.eventType().trim().isEmpty() &&
                event.userId() != null && !event.userId().trim().isEmpty() &&
                event.sessionId() != null && !event.sessionId().trim().isEmpty() &&
                event.timestamp() != null;
    }
}