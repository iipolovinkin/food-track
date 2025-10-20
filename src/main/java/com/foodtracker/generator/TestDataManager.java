package com.foodtracker.generator;

import com.foodtracker.dto.EventRequestDto;
import com.foodtracker.generator.config.GeneratorConfig;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;

@Component
public class TestDataManager {

    private final GeneratorConfig config;
    private final UserGenerator userGenerator;
    private final SessionGenerator sessionGenerator;
    private final EventGenerator eventGenerator;
    private final JourneyBuilder journeyBuilder;
    private final ApiServiceClient apiServiceClient;
    private final AnalyticsValidator analyticsValidator;

    public TestDataManager(
            GeneratorConfig config,
            UserGenerator userGenerator,
            SessionGenerator sessionGenerator,
            EventGenerator eventGenerator,
            JourneyBuilder journeyBuilder,
            ApiServiceClient apiServiceClient,
            AnalyticsValidator analyticsValidator) {
        this.config = config;
        this.userGenerator = userGenerator;
        this.sessionGenerator = sessionGenerator;
        this.eventGenerator = eventGenerator;
        this.journeyBuilder = journeyBuilder;
        this.apiServiceClient = apiServiceClient;
        this.analyticsValidator = analyticsValidator;
    }

    public void generateTestData() {
        System.out.println("Starting test data generation...");
        System.out.println("Generating " + config.getUserCount() + " users");

        // Calculate date range for events
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(config.getDaysFromNow());

        List<String> users = userGenerator.generateUsers(config.getUserCount());
        int successCount = 0;
        int failureCount = 0;

        for (int i = 0; i < users.size(); i++) {
            String userId = users.get(i);

            // Generate 1 to 5 sessions per user
            int sessionCount = sessionGenerator.generateSessionCount(
                    config.getMinSessionsPerUser(),
                    config.getMaxSessionsPerUser()
            );

            for (int j = 0; j < sessionCount; j++) {
                String sessionId = sessionGenerator.generateSessionId();

                LocalDateTime sessionStart = getSessionStart(startDate, endDate);

                List<EventRequestDto> events = generateEvents(userId, sessionId, sessionStart);

                SendEventsResult sendEventsResult = sendEventsToApi(events);

                successCount += sendEventsResult.successCount();
                failureCount += sendEventsResult.failureCount();

            }

            // Print progress every 10 users
            if ((i + 1) % 10 == 0) {
                System.out.println("Processed " + (i + 1) + " users...");
            }
        }

        System.out.println("\nTest data generation completed!");
        System.out.println("Successfully sent: " + successCount + " events");
        System.out.println("Failed to send: " + failureCount + " events");

        // Run analytics validation
        LocalDateTime validationStart = LocalDateTime.of(startDate, LocalTime.MIDNIGHT);
        LocalDateTime validationEnd = LocalDateTime.of(endDate, LocalTime.MAX);
        analyticsValidator.runAllValidations(validationStart, validationEnd);
    }

    private static LocalDateTime getSessionStart(LocalDate startDate, LocalDate endDate) {
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

    private SendEventsResult sendEventsToApi(List<EventRequestDto> events) {
        int successCount = 0;
        int failureCount = 0;
        for (EventRequestDto event : events) {
            if (apiServiceClient.validateEvent(event)) {
                boolean success = apiServiceClient.sendEvent(event);
                if (success) {
                    successCount++;
                    if (config.isVerbose()) {
                        System.out.println("Successfully sent event: " + event.eventType() +
                                " for user " + event.userId());
                    }
                } else {
                    failureCount++;
                    System.err.println("Failed to send event: " + event.eventType() +
                            " for user " + event.userId());
                }
            } else {
                failureCount++;
                System.err.println("Invalid event: " + event.eventType() +
                        " for user " + event.userId());
            }
        }

        return new SendEventsResult(successCount, failureCount);
    }

    private record SendEventsResult(int successCount, int failureCount) {
    }

    private List<EventRequestDto> generateEvents(String userId, String sessionId, LocalDateTime sessionStart) {
        String category = eventGenerator.getRandomCategory();

        // Decide the type of journey for this session
        double journeyType = Math.random();

        List<EventRequestDto> events;
        if (journeyType < 0.2) { // 20% chance of partial journey
            int maxStage = new Random().nextInt(5) + 2; // Stages 2-6
            events = journeyBuilder.buildPartialJourney(userId, sessionId, sessionStart, category, maxStage);
        } else { // 80% chance of complete journey
            events = journeyBuilder.buildCompleteJourney(userId, sessionId, sessionStart, category);
        }
        return events;
    }
}