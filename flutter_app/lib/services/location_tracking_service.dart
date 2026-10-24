import 'dart:convert';

import 'package:geolocator/geolocator.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';

class LocationTrackingService {
  static const double _distanceThresholdMeters = 10;

  String baseUrl = 'http://10.0.2.2:8080';
  String deviceId = 'device-flutter-001';

  Position? _lastPosition;

  Future<bool> shouldSendPosition(Position position) async {
    if (_lastPosition == null) {
      _lastPosition = position;
      return true;
    }

    final distance = Geolocator.distanceBetween(
      _lastPosition!.latitude,
      _lastPosition!.longitude,
      position.latitude,
      position.longitude,
    );

    if (distance >= _distanceThresholdMeters) {
      _lastPosition = position;
      return true;
    }

    return false;
  }

  Future<bool> sendLocation(Position position) async {
    final payload = {
      'deviceId': deviceId,
      'latitude': position.latitude,
      'longitude': position.longitude,
      'accuracyMeters': position.accuracy,
    };

    try {
      final response = await http.post(
        Uri.parse('$baseUrl/api/location/update'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(payload),
      );

      if (response.statusCode >= 200 && response.statusCode < 300) {
        await _saveLatestPosition(position);
        return true;
      }

      return false;
    } catch (_) {
      return false;
    }
  }

  Future<void> _saveLatestPosition(Position position) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setDouble('last_lat', position.latitude);
    await prefs.setDouble('last_lng', position.longitude);
    await prefs.setString('last_time', DateTime.now().toIso8601String());
  }
}
