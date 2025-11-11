# Swagger API Documentation Specification for FoodTracker

## Overview

This document outlines the Swagger/OpenAPI 3.0 specification implementation for the FoodTracker analytics event tracking API. The API provides endpoints for tracking user interactions within a food delivery application and analyzing user behavior through various analytics endpoints.

## API Version and Base URL

- **API Version**: 1.0.0
- **Base URL**: `/api`
- **OpenAPI Version**: 3.0.1
- **Documentation URL**: `http://localhost:8080/swagger-ui.html`

## Technologies Used

- **Framework**: Spring Boot 3.5.6
- **API Documentation**: Springdoc OpenAPI 2.6.0 (Swagger UI)
- **Documentation Format**: OpenAPI 3.0

## Implemented Endpoints

### 1. Event Tracking Endpoint

#### POST `/api/track`
- **Tag**: Event Tracking
- **Summary**: Track a new event
- **Description**: Records a user event with details like event type, user ID, session ID, timestamp, and properties
- **Request Body**: `EventRequestDto`
  - `eventType` (String, required): Type of the event (max 100 characters)
  - `userId` (String, required): Unique user identifier (max 100 characters)
  - `sessionId` (String, required): Session identifier (max 100 characters)
  - `timestamp` (LocalDateTime, required): ISO 8601 formatted timestamp
  - `properties` (Map<String, Object>): Additional event properties
- **Responses**:
  - `200 OK`: Event tracked successfully with ID
  - `400 Bad Request`: Invalid request data
  - `500 Internal Server Error`: Server error during tracking

### 2. Retrieve All Events

#### GET `/api/events`
- **Tag**: Event Tracking
- **Summary**: Get all events
- **Description**: Retrieves a list of all recorded events in the system
- **Query Parameters**: None
- **Responses**:
  - `200 OK`: List of all events retrieved successfully (List<Event>)

### 3. Retrieve Events by Type

#### GET `/api/events/{eventType}`
- **Tag**: Event Tracking
- **Summary**: Get events by type
- **Description**: Retrieves a list of events filtered by event type
- **Path Variables**:
  - `eventType` (String, required): Type of event to filter by (e.g., screen_viewed, item_added_to_cart)
- **Responses**:
  - `200 OK`: List of events retrieved successfully (List<Event>)
  - `400 Bad Request`: Invalid event type parameter

### 4. Retrieve Events by User

#### GET `/api/users/{userId}/events`
- **Tag**: Event Tracking
- **Summary**: Get events by user
- **Description**: Retrieves a list of events for a specific user
- **Path Variables**:
  - `userId` (String, required): ID of the user to retrieve events for
- **Responses**:
  - `200 OK`: List of events retrieved successfully (List<Event>)
  - `400 Bad Request`: Invalid user ID parameter

### 5. Daily Active Users (DAU)

#### GET `/api/analytics/dau`
- **Tag**: Event Tracking
- **Summary**: Get daily active users
- **Description**: Retrieves the count of unique users who performed a specific event type on a given date
- **Query Parameters**:
  - `eventType` (String, required): Type of event to analyze
  - `date` (String, required): Date in ISO format (e.g., 2024-01-01)
- **Responses**:
  - `200 OK`: Daily active user count retrieved successfully (Long)
  - `400 Bad Request`: Invalid request parameters

### 6. Conversion Funnel Analytics

#### GET `/api/analytics/conversion-funnel`
- **Tag**: Event Tracking
- **Summary**: Get conversion funnel analytics
- **Description**: Retrieves conversion funnel metrics for a specific category within a date range
- **Query Parameters**:
  - `category` (String, required): Category to analyze (e.g., pizza, burger)
  - `startDate` (String, required): Start date and time in ISO format (e.g., 2024-01-01T10:00:00)
  - `endDate` (String, required): End date and time in ISO format (e.g., 2024-01-02T10:00:00)
- **Responses**:
  - `200 OK`: Conversion funnel analytics retrieved successfully (ConversionFunnelResponse)
  - `400 Bad Request`: Invalid request parameters

## Data Models

### EventRequestDto
- **eventType**: String (required, max 100 chars) - Type of the event
- **userId**: String (required, max 100 chars) - User identifier
- **sessionId**: String (required, max 100 chars) - Session identifier
- **timestamp**: LocalDateTime (required) - ISO 8601 formatted timestamp
- **properties**: Map<String, Object> (optional) - Additional properties for the event

### Event
- **id**: Long - Unique event identifier
- **eventType**: String - Type of the event
- **userId**: String - User identifier
- **sessionId**: String - Session identifier
- **timestamp**: LocalDateTime - ISO 8601 formatted timestamp
- **properties**: Map<String, Object> - Additional event properties stored as JSON

### ConversionFunnelResponse
- **category**: String - The analyzed category
- **viewedCount**: Long - Number of items viewed
- **addedCount**: Long - Number of items added to cart
- **orderedCount**: Long - Number of items ordered
- **conversionRate**: Double - Conversion rate as a percentage
- **additionalMetrics**: Map<String, Object> - Additional metrics as key-value pairs

## Security Considerations

- No authentication required for basic event tracking endpoints
- Consideration for implementing API rate limiting for event tracking endpoints
- Input validation applied to prevent injection attacks

## Validation Rules

- All string fields have maximum length constraints
- Event timestamps must be in the past or present (not future)
- Required fields are validated with @NotNull and @NotBlank annotations
- Custom validation applied to properties field to prevent unsafe content

## Swagger UI Features

- Interactive API documentation with request/response examples
- Ability to test API endpoints directly from the UI
- Code generation capabilities for different languages
- Downloadable OpenAPI specification in JSON format
- Clear parameter descriptions and example values

## Usage Instructions

To access the API documentation:

1. Start the application server
2. Navigate to `http://localhost:8080/swagger-ui.html`
3. Explore available endpoints with detailed descriptions
4. Test endpoints directly from the UI interface
5. Download the OpenAPI specification from `http://localhost:8080/v3/api-docs`

## Future Enhancements

- Add OAuth 2.0 security schemes if authentication is implemented
- Include more detailed request/response examples
- Add error codes and their meanings documentation
- Implement request/response validation through schema definitions
- Add support for different response formats (JSON, XML)