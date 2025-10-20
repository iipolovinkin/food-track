package com.foodtracker.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.foodtracker.dto.EventRequestDto;
import com.foodtracker.generator.config.GeneratorConfig;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component
public class ApiServiceClient {

    private final GeneratorConfig config;
    private final ObjectMapper objectMapper;

    public ApiServiceClient(GeneratorConfig config) {
        this.config = config;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public boolean sendEvent(EventRequestDto event) {
        try {
            URL url = new URL(config.getApiBaseUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            String jsonInput = objectMapper.writeValueAsString(event);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            return responseCode == 200;

        } catch (Exception e) {
            System.err.println("Error sending event to API: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean validateEvent(EventRequestDto event) {
        // Perform basic validation of the event
        return event.eventType() != null && !event.eventType().trim().isEmpty() &&
                event.userId() != null && !event.userId().trim().isEmpty() &&
                event.sessionId() != null && !event.sessionId().trim().isEmpty() &&
                event.timestamp() != null;
    }
}