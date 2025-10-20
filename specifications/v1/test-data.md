# Test Data Generation Specification for FoodTrack

## 1. Overview

This document specifies the test data generation strategy for the FoodTrack analytics system. The goal is to create comprehensive, realistic test data that validates both the event tracking API functionality and the analytics query capabilities.

## 2. Test Data Requirements

### 2.1 Event Types Coverage
The test data must include all defined event types:
- `app_opened` - App launch events
- `screen_viewed` - Screen navigation events
- `item_viewed` - Product viewing events
- `item_added_to_cart` - Cart addition events
- `checkout_started` - Checkout initiation events
- `order_placed` - Successful order events
- `payment_failed` - Failed payment events

### 2.2 Category Coverage
Test data must include events for both food categories:
- Pizza category events
- Burger category events

### 2.3 User Journey Scenarios
The data generation must simulate realistic user journeys through the conversion funnel:
- Complete funnels: opening → viewing → adding to cart → ordering
- Partial funnels: users dropping off at different stages
- Error scenarios: payment failures

## 3. Data Generation Strategy

### 3.1 User Generation
- Generate 50-100 unique user IDs in the format `user_<number>`
- Each user represents a real application user

### 3.2 Session Generation
- Each user will have 1-5 sessions with unique session IDs
- Session IDs follow the format `sess_<alphanumeric>`

### 3.3 Event Sequencing
- Events must follow chronological order with realistic time intervals
- Each user journey should have logical progression through the funnel
- Time intervals between events should be realistic (seconds to minutes)

## 4. Test Scenarios

### 4.1 Complete Funnel Scenarios
- Create 10-15 users that complete the full funnel for pizza
- Create 10-15 users that complete the full funnel for burgers
- Each complete journey includes all 7 event types in sequence

### 4.2 Partial Funnel Scenarios
- 20 users who view items but don't add to cart
- 15 users who add to cart but don't start checkout
- 10 users who start checkout but don't place order
- 5 users who experience payment failures

### 4.3 Volume Scenarios
- Generate sufficient data volume (minimum 500 events) for meaningful analytics
- Distribute events across multiple days for DAU testing

## 5. Analytics Validation

### 5.1 DAU (Daily Active Users) Validation
- Generate `app_opened` events distributed across multiple days
- Ensure sufficient user activity per day for meaningful DAU calculations

### 5.2 Conversion Funnel Validation
- Generate data to test pizza category funnel: viewed → added → ordered
- Generate data to test burger category funnel: viewed → added → ordered
- Calculate expected conversion rates for validation

### 5.3 Popular Items Validation
- Generate multiple `item_viewed` events for popular items
- Ensure top 5 popular items can be identified from the data

### 5.4 Cart-to-Order Conversion Rate Validation
- Generate `checkout_started` and `order_placed` events
- Include some payment failures to test conversion rate calculations with errors

## 6. Data Structure Examples

### 6.1 App Opened Event
```json
{
  "eventType": "app_opened",
  "userId": "user_001",
  "sessionId": "sess_x1y2z3",
  "timestamp": "2024-05-20T10:00:00",
  "properties": {
    "platform": "android",
    "version": "1.2.0"
  }
}
```

### 6.2 Item Viewed Event
```json
{
  "eventType": "item_viewed",
  "userId": "user_001",
  "sessionId": "sess_x1y2z3",
  "timestamp": "2024-05-20T10:05:00",
  "properties": {
    "item_id": "pizza_pepperoni",
    "category": "pizza",
    "price": 599
  }
}
```

### 6.3 Order Placed Event
```json
{
  "eventType": "order_placed",
  "userId": "user_001",
  "sessionId": "sess_x1y2z3",
  "timestamp": "2024-05-20T10:25:00",
  "properties": {
    "order_id": "ord_12345",
    "total": 1198,
    "payment_method": "card"
  }
}
```

## 7. Implementation Approach

### 7.1 Data Generator Tool
Create a Java-based test data generator that:
- Creates realistic event sequences following the schema
- Sends events via the `/api/track` endpoint
- Validates API responses
- Tracks generated data for later analytics verification

### 7.2 Data Validation
- Verify all events are stored correctly in the database
- Test all analytics endpoints with the generated data
- Validate expected vs. actual analytics results