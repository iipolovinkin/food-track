package com.foodtracker.generator;

import com.foodtracker.generator.config.GeneratorConfig;
import com.foodtracker.api.tracking.TrackingEventRequestDto;
import com.foodtracker.generator.gateway.tracking.TrackingGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;
import java.util.function.BinaryOperator;
import java.util.stream.IntStream;

@AllArgsConstructor
@Component
@Slf4j
public class TestDataManager {

    private final GeneratorConfig config;
    private final UserGenerator userGenerator;
    private final SessionGenerator sessionGenerator;
    private final EventGenerator eventGenerator;
    private final JourneyBuilder journeyBuilder;
    private final TrackingGateway trackingGateway;
    private final AnalyticsValidator analyticsValidator;

    public void generateTestData() {
        long startTime = System.nanoTime();
        log.info("Starting test data generation...");
        log.info("Generating {} users", config.getUserCount());

        // Calculate date range for events
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(config.getDaysFromNow());

        List<String> users = userGenerator.generateUsers(config.getUserCount());

        LocalDateTime sessionStart = getSessionStart(startDate, endDate);

        SendEventsStats sendEventsStats = users.parallelStream()
                .map(userId -> generateDataForUser(userId, sessionStart))
                .reduce(SendEventsStats.getAccumulator())
                .get();

        log.info("\nTest data generation completed!");
        log.info("Successfully sent: {} events", sendEventsStats.successCount());
        log.info("Failed to send: {} events", sendEventsStats.failureCount());

        long endTime = System.nanoTime();
        long durationNanos = endTime - startTime;
        double durationSeconds = durationNanos / 1_000_000_000.0; // Convert nanoseconds to seconds

        log.info("Total execution time: {} seconds", String.format("%.2f", durationSeconds));

        // Run analytics validation
        LocalDateTime validationStart = LocalDateTime.of(startDate, LocalTime.MIDNIGHT);
        LocalDateTime validationEnd = LocalDateTime.of(endDate, LocalTime.MAX);
        analyticsValidator.runAllValidations(validationStart, validationEnd);
    }

    private SendEventsStats generateDataForUser(String userId, LocalDateTime sessionStart) {
        int sessionCount = sessionGenerator.generateSessionCount(
                config.getMinSessionsPerUser(),
                config.getMaxSessionsPerUser()
        );

        return IntStream.range(0, sessionCount)
                .boxed()
                .map(item -> sessionGenerator.generateSessionId())
                .map(s -> generateEvents(userId, s, sessionStart))
                .map(this::sendEventsToApi)
                .reduce(SendEventsStats.getAccumulator())
                .get();
    }

    private LocalDateTime getSessionStart(LocalDate startDate, LocalDate endDate) {
        // Select a random date within the range for this session
        long daysBetween = startDate.until(endDate).getDays();
        long randomDays = new Random().nextInt((int) daysBetween + 1);
        LocalDate sessionDate = startDate.plusDays(randomDays);

        // Start session at a random time during the day
        LocalTime randomTime = LocalTime.of(
                new Random().nextInt(24),
                new Random().nextInt(60)
        );
        return LocalDateTime.of(sessionDate, randomTime);
    }

    private SendEventsStats sendEventsToApi(List<TrackingEventRequestDto> events) {
        int successCount = 0;
        int failureCount = 0;
        for (TrackingEventRequestDto event : events) {
            if (trackingGateway.validateEvent(event)) {
                boolean success = trackingGateway.sendEvent(event);
                if (success) {
                    successCount++;
                    if (config.isVerbose()) {
                        log.debug("Successfully sent event: {} for user {}", event.eventType(), event.userId());
                    }
                } else {
                    failureCount++;
                    log.error("Failed to send event: {} for user {}", event.eventType(), event.userId());
                }
            } else {
                failureCount++;
                log.error("Invalid event: {} for user {}", event.eventType(), event.userId());
            }
        }

        return new SendEventsStats(successCount, failureCount);
    }

    private record SendEventsStats(int successCount, int failureCount) {
        public static BinaryOperator<SendEventsStats> getAccumulator() {
            return (s1, s2) -> new SendEventsStats(
                    s1.successCount() + s2.successCount(),
                    s1.failureCount() + s2.failureCount()
            );
        }
    }

    private List<TrackingEventRequestDto> generateEvents(String userId, String sessionId, LocalDateTime sessionStart) {
        String category = eventGenerator.getRandomCategory();

        // Decide the type of journey for this session
        double journeyType = Math.random();

        List<TrackingEventRequestDto> events;
        if (journeyType < 0.2) { // 20% chance of partial journey
            int maxStage = new Random().nextInt(5) + 2; // Stages 2-6
            events = journeyBuilder.buildPartialJourney(userId, sessionId, sessionStart, category, maxStage);
        } else { // 80% chance of complete journey
            events = journeyBuilder.buildCompleteJourney(userId, sessionId, sessionStart, category);
        }
        return events;
    }
}