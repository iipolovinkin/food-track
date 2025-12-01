package com.foodtracker.api.analytics;

import com.foodtracker.config.AnalyticsConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of AnalyticsGateway that uses WebClient for HTTP calls to analytics endpoints.
 * Provides better performance through connection pooling and improved HTTP handling compared to HttpURLConnection.
 * NOTE: Currently using .block() for compatibility with existing synchronous interface.
 * In future, consider migrating to fully reactive approach.
 */
@Component
@Slf4j
public class WebClientAnalyticsGatewayImpl implements AnalyticsGateway {

    public static final String PATH = "/api/analytics";
    private final WebClient webClient;

    public WebClientAnalyticsGatewayImpl(AnalyticsConfig analyticsConfig) {

        this.webClient = WebClient.builder()
                .baseUrl(analyticsConfig.getApiBaseUrl())
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10MB max
                .build();
    }

    @Override
    public Long getDailyActiveUsers(String eventType, LocalDate date) {
        try {
            String url = UriComponentsBuilder.fromPath(PATH + "/dau")
                    .queryParam("eventType", eventType)
                    .queryParam("date", date.toString())
                    .toUriString();

            return webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Long.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();
        } catch (WebClientResponseException e) {
            log.error("HTTP error getting daily active users (status {}):", e.getStatusCode(), e);
            return 0L;
        } catch (Exception e) {
            log.error("Error getting daily active users:", e);
            return 0L;
        }
    }

    @Override
    public ConversionFunnelResponse getConversionFunnel(String category, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            String url = UriComponentsBuilder.fromPath(PATH + "/conversion-funnel")
                    .queryParam("category", category)
                    .queryParam("startDate", startDate.toString())
                    .queryParam("endDate", endDate.toString())
                    .toUriString();

            return webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(ConversionFunnelResponse.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();
        } catch (WebClientResponseException e) {
            log.error("HTTP error getting conversion funnel (status {}):", e.getStatusCode(), e);
            return null;
        } catch (Exception e) {
            log.error("Error getting conversion funnel: ", e);
            return null;
        }
    }

    @Override
    public List<TrackEventDto> getAllEvents() {
        try {
            // Use array approach to handle interface deserialization correctly
            TrackEventDto[] eventsArray = webClient.get()
                    .uri(PATH + "/events")
                    .retrieve()
                    .bodyToMono(TrackEventDto[].class)
                    .timeout(Duration.ofSeconds(30))
                    .block();
            if (eventsArray == null) {
                return List.of();
            }
            return List.of(eventsArray);
        } catch (WebClientResponseException e) {
            log.error("HTTP error getting all events (status {}):", e.getStatusCode(), e);
            return List.of();
        } catch (Exception e) {
            log.error("Error getting all events: ", e);
            return List.of();
        }
    }

    @Override
    public List<TrackEvent> getEventsByType(String eventType) {
        try {
            // Use array approach to handle interface deserialization correctly
            TrackEventDto[] eventsArray = webClient.get()
                    .uri(PATH + "/events/" + eventType)
                    .retrieve()
                    .bodyToMono(TrackEventDto[].class)
                    .timeout(Duration.ofSeconds(30))
                    .block();
            if (eventsArray == null) {
                return List.of();
            }
            return Arrays.asList(eventsArray);
        } catch (WebClientResponseException e) {
            log.error("HTTP error getting events by type (status {}):", e.getStatusCode(), e);
            return List.of();
        } catch (Exception e) {
            log.error("Error getting events by type: ", e);
            return List.of();
        }
    }

    @Override
    public List<TrackEvent> getEventsByUser(String userId) {
        try {
            // Use array approach to handle interface deserialization correctly
            TrackEventDto[] eventsArray = webClient.get()
                    .uri(PATH + "/users/" + userId + "/events")
                    .retrieve()
                    .bodyToMono(TrackEventDto[].class)
                    .timeout(Duration.ofSeconds(30))
                    .block();
            return Arrays.asList(eventsArray);
        } catch (WebClientResponseException e) {
            log.error("HTTP error getting events by user (status {}):", e.getStatusCode(), e);
            return List.of();
        } catch (Exception e) {
            log.error("Error getting events by user: ", e);
            return List.of();
        }
    }
}