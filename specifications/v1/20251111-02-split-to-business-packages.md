# FoodTrack Project Restructuring: Split into Analytics and Tracking Packages

## Overview
This document outlines the restructuring of the FoodTrack project to separate concerns between tracking and analytics functionality. The main goal is to improve maintainability, scalability, and code organization.

## Motivation
The original FoodTrack application was a monolithic structure where tracking and analytics functionality were mixed together. This made it difficult to:
- Understand and maintain the codebase
- Scale different components independently  
- Allow different teams to work on tracking vs analytics features
- Implement separate deployment strategies

## New Package Structure
After restructuring, the application follows this organization:

```
com.foodtracker/
├── config/
├── tracking/
│   ├── controller/
│   │   ├── EventController.java (tracking-specific endpoints)
│   ├── service/
│   │   ├── EventService.java (tracking-specific interface)
│   │   └── EventServiceImpl.java (tracking-specific implementation)
│   └── dto/
│       └── (tracking-specific DTOs if needed)
├── analytics/
│   ├── controller/
│   │   └── AnalyticsController.java (analytics-specific endpoints)  
│   ├── service/
│   │   ├── AnalyticsService.java (analytics-specific interface)
│   │   └── AnalyticsServiceImpl.java (analytics-specific implementation)
│   └── dto/
│       └── ConversionFunnelResponse.java (analytics-specific DTO)
├── shared/
│   ├── model/
│   │   └── Event.java (shared model)
│   ├── repository/
│   │   └── EventRepository.java (shared repository)
│   ├── util/
│   └── validation/
├── exception/
├── generator/
└── FoodTrackerApplication.java
```

## Changes Made

### Tracking Package
- **Controllers**: `EventController` now contains only tracking-related endpoints such as `/track`, `/events`, `/users/{userId}/events`
- **Services**: `EventService` and `EventServiceImpl` contain only tracking methods: `trackEvent()`, `getAllEvents()`, `getEventsByType()`, `getEventsByUser()`
- **Responsibility**: Focuses solely on event collection and storage

### Analytics Package  
- **Controllers**: `AnalyticsController` contains only analytics-related endpoints such as `/analytics/dau`, `/analytics/conversion-funnel`
- **Services**: `AnalyticsService` and `AnalyticsServiceImpl` contain only analytics methods: `getDailyActiveUsers()`, `getConversionFunnelAnalytics()`
- **DTOs**: `ConversionFunnelResponse` moved here as it's analytics-specific
- **Responsibility**: Focuses solely on data analysis and reporting

### Shared Package
- **Models**: `Event.java` moved to shared as it's needed by both tracking and analytics
- **Repository**: `EventRepository.java` moved to shared as it's used by both tracking and analytics
- **Purpose**: Contains components that are shared between tracking and analytics packages

## API Endpoint Changes
The API endpoints remain functionally the same but are now organized by concern:
- **Tracking endpoints** (previously in `/api/`): 
  - `/api/track`
  - `/api/events`
  - `/api/events/{eventType}`
  - `/api/users/{userId}/events`
- **Analytics endpoints** (moved to `/api/analytics/`):
  - `/api/analytics/dau`
  - `/api/analytics/conversion-funnel`

## Backward Compatibility
- All existing API endpoints remain accessible with the same URLs
- Data models and repository queries remain unchanged
- This refactoring is purely structural and does not affect functionality

## Impact on Tests
- Integration tests for tracking endpoints have been updated to reflect the new package structure
- Service-level tests remain the same, just with updated imports
- Architecture tests have been relocated to appropriate directories

## Build and Runtime Considerations
- The application should build and run without issues
- Spring's component scanning automatically discovers components in all sub-packages
- No changes required to configuration or deployment files