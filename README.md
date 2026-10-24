# Java Mobile Location Tracking Starter

This repository contains a starter implementation for a mobile tracking application with:
- Android Java app that sends GPS updates from the device
- Spring Boot admin backend that receives and stores location updates
- Simple admin API for tracking devices in near real-time

## How it works

1. The Android app reads GPS coordinates using `FusedLocationProviderClient`.
2. It compares the current GPS location with the last saved location.
3. If the phone moves by a configured threshold (for example 10 meters), it sends the update to the backend.
4. The backend stores the latest position and exposes endpoints for the admin dashboard.

## Recommended update rule

Use a movement threshold such as 10 to 20 meters to reduce battery usage and avoid noisy updates.

## Mobile app requirements

- Android device with GPS enabled
- Internet access for sending updates to backend
- Background location permission

## Backend requirements

- Java 17+
- Maven
- Spring Boot 3.x

## Run backend

```bash
cd backend
mvn spring-boot:run
```

The app will expose:
- `POST /api/location/update`
- `GET /api/location/all`
- `GET /api/location/{deviceId}`

## Update the server URL in the Android app

In `LocationTrackerService.java`, replace the URL with your backend base URL:

```java
private static final String BASE_URL = "https://your-backend-url.com";
```

## Important notes

- Use HTTPS in production.
- Use a user/device ID to identify each phone.
- Use a proper database in production instead of in-memory storage.
- For real-time admin updates, add WebSockets or polling from the dashboard.
- For battery efficiency, use foreground service or location batching.

This is a starter project, not a production-ready fleet tracking system.
