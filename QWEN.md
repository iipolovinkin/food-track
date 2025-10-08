# FoodTrack - Analytics for Food Startup

## Project Overview

FoodTrack is an analytics event tracking system designed for a food delivery app with pizza and burger categories. The primary goal is to understand user behavior and measure the effectiveness of the conversion funnel from opening the app to placing an order.

The project implements an **Event Tracker** focusing on tracking user interactions across two categories: pizza and burgers. The core objective is to measure the user journey through the **funnel: opening → viewing → adding to cart → ordering**.

### Key Features
- **Unified Event Structure**: Uses a flexible JSON-based event format suitable for all event types
- **Flexible Properties**: Supports `properties` as JSON objects for contextual data
- **PostgreSQL Storage**: Stores events in PostgreSQL using JSONB for flexible querying
- **Analytics Support**: Provides metrics like DAU, conversion funnels, and popularity analysis

### Tech Stack
- **Backend**: Java (JDK 21)
- **Database**: PostgreSQL with JSONB support
- **IDE**: IntelliJ IDEA (project files in `.idea/` directory)

## Event Structure

The system uses a unified, flexible event format:

```json
{
  "eventType": "screen_viewed",
  "userId": "user_789",
  "sessionId": "sess_abc123",
  "timestamp": "2024-05-20T18:42:10Z",
  "properties": {
    "screen": "menu",
    "category": "pizza"
  }
}
```

### Mandatory Fields
| Field | Type | Description |
|-------|------|-------------|
| `eventType` | string | Event name (`screen_viewed`, `item_added`, etc.) |
| `userId` | string | Unique user ID (temporary for unauthenticated users) |
| `sessionId` | string | Session ID (useful for tracking user path) |
| `timestamp` | ISO 8601 | Event time (preferably generated on client) |

### Optional Fields
Any contextual data can be stored in `properties`: `item_id`, `price`, `category`, `screen`, `error_code`, etc.

## Tracked Events

The system monitors 5-6 key events for funnel and behavior analytics:

| Event | Trigger | Example Properties |
|-------|---------|-------------------|
| `app_opened` | App launch | `{ "platform": "android", "version": "1.2.0" }` |
| `screen_viewed` | Screen opened | `{ "screen": "menu", "category": "pizza" }` |
| `item_viewed` | Item card opened | `{ "item_id": "pizza_pepperoni", "category": "pizza", "price": 599 }` |
| `item_added_to_cart` | Added to cart | `{ "item_id": "burger_classic", "category": "burger", "quantity": 2 }` |
| `checkout_started` | Started checkout | `{ "cart_total": 1198, "items_count": 2 }` |
| `order_placed` | Order completed | `{ "order_id": "ord_12345", "total": 1198, "payment_method": "card" }` |
| `payment_failed` | Payment failed | `{ "error_code": "card_declined", "order_id": "ord_12345" }` |

## Database Schema

The system uses a single `events` table in PostgreSQL:

```sql
CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    session_id VARCHAR(100) NOT NULL,
    timestamp TIMESTAMPTZ NOT NULL,
    properties JSONB
);

-- Indexes for analytics
CREATE INDEX idx_events_type_time ON events (event_type, timestamp);
CREATE INDEX idx_events_user ON events (user_id);
CREATE INDEX idx_events_props_category ON events ((properties->>'category'));
```

## Available Metrics

Based on the tracked events, the system can compute:

1. **DAU (Daily Active Users)**:
   ```sql
   SELECT COUNT(DISTINCT user_id) 
   FROM events 
   WHERE event_type = 'app_opened' 
     AND timestamp >= '2024-05-20';
   ```

2. **Conversion Funnels**: Track unique users moving from category viewing → adding → ordering

3. **Top 5 Popular Items**:
   ```sql
   SELECT properties->>'item_id' AS item, COUNT(*) 
   FROM events 
   WHERE event_type = 'item_viewed' 
   GROUP BY item 
   ORDER BY COUNT DESC 
   LIMIT 5;
   ```

4. **Cart-to-Order Conversion Rate**: 
   `(order_placed count) / (checkout_started count) * 100%`

## Building and Running

Since this is a Java project using JDK 21:

### Prerequisites
- Java Development Kit (JDK) 21
- PostgreSQL database server

### Setup Instructions
1. Initialize the PostgreSQL database with the schema provided above
2. Configure database connection settings
3. The project likely uses Maven or Gradle for dependency management (though configuration files may not be committed to the repository yet)

### For Developers
- The project is configured for IntelliJ IDEA (as shown by `.idea/` directory)
- The Maven runner is configured in `.idea/misc.xml`
- Currently no build configuration files (pom.xml or build.gradle) are visible in the repository

## Development Conventions

- **Event Naming**: Use snake_case for event types (e.g., `app_opened`, `item_added_to_cart`)
- **Properties**: Use camelCase for property keys in JSON objects
- **Timestamps**: Use ISO 8601 format for all timestamps
- **Category Tracking**: Include a `category` field in properties to distinguish between pizza and burger events

## Testing

The README mentions that demo data can be generated using a script that creates N users with event chains. This would be useful for testing the analytics capabilities.