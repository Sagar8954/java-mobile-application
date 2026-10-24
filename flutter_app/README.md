# Flutter location tracking app

This Flutter app replaces the previous Android Java tracker and streams GPS coordinates to the Spring Boot backend in near real time.

## Features

- Request location permissions
- Track live GPS locations
- Only send data when the phone moves more than a configured threshold
- Send location updates to the Spring Boot admin backend
- Display current latitude, longitude, accuracy, and status

## Run

1. Install Flutter SDK.
2. Open the project:

```bash
cd flutter_app
flutter pub get
flutter run
```

## Backend URL

By default the app connects to:

```text
http://10.0.2.2:8080
```

This works for Android emulator.

For a physical phone on the same Wi‑Fi network, change the URL in `lib/main.dart` or `lib/services/location_tracking_service.dart` to your laptop/server IP.

Example:

```text
http://192.168.1.25:8080
```

## Important notes

- Use HTTPS in production.
- Set a real device ID per phone.
- Persist the last known position in a real database in production.
- For a full admin dashboard, use a map library such as Google Maps or Mapbox.
