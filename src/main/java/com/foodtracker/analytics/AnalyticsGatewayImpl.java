package com.foodtracker.analytics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.foodtracker.analytics.dto.ConversionFunnelResponse;
import com.foodtracker.config.AnalyticsConfig;
import com.foodtracker.shared.model.Event;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of AnalyticsGateway that makes HTTP calls to analytics endpoints.
 */
@Component
public class AnalyticsGatewayImpl implements AnalyticsGateway {

    private final AnalyticsConfig analyticsConfig;
    private final ObjectMapper objectMapper;

    public AnalyticsGatewayImpl(AnalyticsConfig analyticsConfig) {
        this.analyticsConfig = analyticsConfig;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public Long getDailyActiveUsers(String eventType, LocalDate date) {
        try {
            String urlString = analyticsConfig.getApiBaseUrl() + "/api/analytics/dau?eventType=" + eventType + "&date=" + date.toString();
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return Long.valueOf(response.toString());
            } else {
                System.err.println("Error getting DAU: HTTP " + responseCode);
                return 0L;
            }
        } catch (Exception e) {
            System.err.println("Error getting daily active users: " + e.getMessage());
            e.printStackTrace();
            return 0L;
        }
    }

    @Override
    public ConversionFunnelResponse getConversionFunnel(String category, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            String urlString = analyticsConfig.getApiBaseUrl() + "/api/analytics/conversion-funnel?" +
                    "category=" + category +
                    "&startDate=" + startDate.toString() +
                    "&endDate=" + endDate.toString();

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return objectMapper.readValue(response.toString(), ConversionFunnelResponse.class);
            } else {
                System.err.println("Error getting conversion funnel: HTTP " + responseCode);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error getting conversion funnel: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Event> getAllEvents() {
        try {
            String urlString = analyticsConfig.getApiBaseUrl() + "/api/analytics/events";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse the JSON response to list of events
                Event[] eventsArray = objectMapper.readValue(response.toString(), Event[].class);
                return Arrays.asList(eventsArray);
            } else {
                System.err.println("Error getting all events: HTTP " + responseCode);
                return Arrays.asList();
            }
        } catch (Exception e) {
            System.err.println("Error getting all events: " + e.getMessage());
            e.printStackTrace();
            return Arrays.asList();
        }
    }

    @Override
    public List<Event> getEventsByType(String eventType) {
        try {
            String urlString = analyticsConfig.getApiBaseUrl() + "/api/analytics/events/" + eventType;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse the JSON response to list of events
                Event[] eventsArray = objectMapper.readValue(response.toString(), Event[].class);
                return Arrays.asList(eventsArray);
            } else {
                System.err.println("Error getting events by type: HTTP " + responseCode);
                return Arrays.asList();
            }
        } catch (Exception e) {
            System.err.println("Error getting events by type: " + e.getMessage());
            e.printStackTrace();
            return Arrays.asList();
        }
    }

    @Override
    public List<Event> getEventsByUser(String userId) {
        try {
            String urlString = analyticsConfig.getApiBaseUrl() + "/api/analytics/users/" + userId + "/events";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse the JSON response to list of events
                Event[] eventsArray = objectMapper.readValue(response.toString(), Event[].class);
                return Arrays.asList(eventsArray);
            } else {
                System.err.println("Error getting events by user: HTTP " + responseCode);
                return Arrays.asList();
            }
        } catch (Exception e) {
            System.err.println("Error getting events by user: " + e.getMessage());
            e.printStackTrace();
            return Arrays.asList();
        }
    }
}