# FoodTrack - Analytics Event Tracking System

## Project Overview

FoodTrack is an analytics event tracking system built with Java Spring Boot for a food delivery application. It tracks user interactions across pizza and burger categories to analyze the conversion funnel from opening the app to placing an order. The system uses PostgreSQL with JSONB for flexible event storage and enables analytics like DAU, conversion funnels, and popular item identification.

## Technology Stack

- **Backend**: Java 21 with Spring Boot 3.5.6
- **Database**: PostgreSQL with JSONB support for flexible property storage
- **Dependencies**: 
  - Spring Boot Web, Data JPA, Security
  - Lombok for reducing boilerplate code
  - Hypersistence Utils for JSONB handling
  - SpringDoc OpenAPI for API documentation
  - PostgreSQL driver
- **Build Tool**: Maven

## Architecture

The application follows a typical Spring Boot architecture with the following packages:

### Core Components
- **Controller**: REST API endpoints in `EventController.java` handling event tracking and analytics
- **Service**: Business logic in `EventService.java` with data validation and processing
- **Model**: JPA entity `Event.java` representing stored events
- **Repository**: Data access layer with JPA repositories
- **DTOs**: Data transfer objects for API requests and responses
- **Config**: Configuration classes
- **Validation**: Custom validation annotations and utilities
- **Util**: Utility classes
- **Generator**: Test data generation components

### Event Tracking API
The system exposes a `/api/track` endpoint that accepts events with the following structure:
```json
{
  "eventType": "screen_viewed",
  "userId": "user_789",
  "sessionId": "sess_abc123",
  "timestamp": "2024-05-20T18:42:10",
  "properties": {
    "screen": "menu",
    "category": "pizza"
  }
}
```

## Key Features

### 1. Flexible Event Schema
- Universal event format with mandatory fields: eventType, userId, sessionId, timestamp
- Flexible properties stored as JSONB for additional contextual data
- Supports all required event types: app_opened, screen_viewed, item_viewed, item_added_to_cart, checkout_started, order_placed, payment_failed

### 2. Analytics Capabilities
- Daily Active Users (DAU) calculation
- Conversion funnel analytics for pizza and burger categories
- Popular items identification
- Cart-to-order conversion rate metrics

### 3. Test Data Generation
- Comprehensive test data generator with realistic user journeys
- Configurable number of users and sessions
- Simulated conversion funnels and error scenarios
- Analytics validation capabilities

## Database Schema

The system uses a single `events` table with these columns:
- id: BIGSERIAL PRIMARY KEY
- event_type: VARCHAR(100) NOT NULL
- user_id: VARCHAR(100) NOT NULL
- session_id: VARCHAR(100) NOT NULL
- timestamp: TIMESTAMPTZ NOT NULL
- properties: JSONB

Optimized with indexes on event_type+timestamp, user_id, and category property.

## Building and Running

### Prerequisites
- Java 21
- PostgreSQL database server

### Setup Instructions
1. Create a PostgreSQL database named `foodtracker`
2. Update `application.properties` with correct database credentials if needed
3. Build the application: `mvn clean install`
4. Run the application: `mvn spring-boot:run` or `java -jar target/food-tracker-0.0.1-SNAPSHOT.jar`

### Test Data Generation
To generate test data, run the application with the argument `--generate-test-data`:
`java -jar target/food-tracker-0.0.1-SNAPSHOT.jar --generate-test-data`

## API Endpoints

- `POST /api/track` - Track a new event
- `GET /api/events` - Get all events
- `GET /api/events/{eventType}` - Get events by type
- `GET /api/users/{userId}/events` - Get events by user
- `GET /api/analytics/dau?eventType=X&date=Y` - Get daily active users
- `GET /api/analytics/conversion-funnel?category=X&startDate=Y&endDate=Z` - Get conversion funnel analytics

## Development Conventions

- Uses Lombok annotations to reduce boilerplate code
- Implements comprehensive validation for input data
- Follows REST API best practices with proper HTTP status codes
- Uses SLF4J with Lombok's @Slf4j annotation for logging
- Implements proper error handling and validation
- Uses record classes for immutable DTOs
- Follows Spring Boot configuration and security best practices

## Project Structure

```
src/
├── main/
│   ├── java/com/foodtracker/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST API controllers
│   │   ├── dto/             # Data transfer objects
│   │   ├── exception/       # Custom exceptions
│   │   ├── generator/       # Test data generation components
│   │   ├── model/           # JPA entities
│   │   ├── repository/      # JPA repositories
│   │   ├── service/         # Business logic services
│   │   ├── util/            # Utility classes
│   │   ├── validation/      # Custom validation annotations
│   │   └── FoodTrackerApplication.java  # Main application class
│   └── resources/
│       ├── application.properties         # Main configuration
│       └── application-local.properties   # Local development configuration
├── specifications/          # Project specifications
└── test/                  # Test files
```

## Testing

The project uses Spring Boot's testing framework with JUnit 5. Test classes are located in the `src/test` directory with the same package structure as the main source code.

## Additional Features

- OpenAPI (Swagger) documentation available at `/swagger-ui.html` and `/v3/api-docs`
- Security implemented with Spring Security
- JSON validation using Jackson and custom validation annotations
- Database schema generation using JPA's ddl-auto=update setting
- Comprehensive logging throughout the application