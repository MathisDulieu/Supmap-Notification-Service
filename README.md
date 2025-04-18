# Notification-Service

## Description
User notification preference management service for SUPMAP that enables customization of communication channels
and alert delivery. Provides personalized notification settings with metrics tracking for engagement optimization,
allowing users to control how they receive traffic updates, incident reports, and system communications through email
and other channels.

## Features
- Real-time traffic incident notifications
- Route change alerts when traffic conditions change
- Proximity-based notifications for nearby incidents
- User preference-based notification filtering
- Push notification delivery to mobile devices
- Notification history and management
- Geofence-triggered alerts

## Tech Stack
- Java 21
- Spring Boot 3.4.4
- MongoDB for notification data storage
- Kafka for event-driven notification processing
- Prometheus for monitoring and metrics collection

## Dependencies
- shared-models: Common data models for the SUPMAP ecosystem
- database-utils: Database utility functions
- Spring Boot Web for RESTful API endpoints
- MongoDB for notification persistence
- Kafka for receiving real-time events from other services
- Prometheus for metrics and monitoring

## Configuration
The service can be configured via environment variables:

```yaml
supmap:
 properties:
   database-name: notification_service_db
   elasticsearch-password: your-password
   elasticsearch-url: http://elasticsearch:9200
   elasticsearch-username: elastic
   kafka-bootstrap-servers: kafka:9092
   mongo-uri: mongodb://user:password@mongodb:27017/notification_service_db