import 'dart:async';
import 'dart:developer' as developer;

import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart';
import 'package:intl/intl.dart';

import 'services/location_tracking_service.dart';

void main() {
  runApp(const LocationTrackerApp());
}

class LocationTrackerApp extends StatelessWidget {
  const LocationTrackerApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Location Tracker',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.indigo),
        useMaterial3: true,
      ),
      home: const TrackerHomeScreen(),
      debugShowCheckedModeBanner: false,
    );
  }
}

class TrackerHomeScreen extends StatefulWidget {
  const TrackerHomeScreen({super.key});

  @override
  State<TrackerHomeScreen> createState() => _TrackerHomeScreenState();
}

class _TrackerHomeScreenState extends State<TrackerHomeScreen> {
  final LocationTrackingService _trackingService = LocationTrackingService();
  final TextEditingController _apiController = TextEditingController(
    text: 'http://10.0.2.2:8080',
  );

  final TextEditingController _deviceController = TextEditingController(
    text: 'device-flutter-001',
  );

  String _status = 'Waiting for GPS';
  String _coordinates = 'Latitude: --\nLongitude: --';
  String _lastUpdated = 'Not sent yet';
  String _accuracy = 'Accuracy: --';
  bool _isTracking = false;

  StreamSubscription<Position>? _positionSubscription;

  @override
  void initState() {
    super.initState();
    _trackingService.baseUrl = _apiController.text;
    _trackingService.deviceId = _deviceController.text;
    _requestPermissionsAndStart();
  }

  Future<void> _requestPermissionsAndStart() async {
    final permission = await Geolocator.requestPermission();

    if (permission == LocationPermission.denied ||
        permission == LocationPermission.deniedForever) {
      setState(() {
        _status = 'Location permission was denied.';
      });
      return;
    }

    await _startLocationTracking();
  }

  Future<void> _startLocationTracking() async {
    setState(() {
      _isTracking = true;
      _status = 'Tracking active';
    });

    _trackingService.baseUrl = _apiController.text;
    _trackingService.deviceId = _deviceController.text;

    _positionSubscription = Geolocator.getPositionStream(
      locationSettings: const LocationSettings(
        accuracy: LocationAccuracy.high,
        distanceFilter: 10,
      ),
    ).listen(
      (Position position) async {
        final moved = await _trackingService.shouldSendPosition(position);
        if (!moved) {
          return;
        }

        final result = await _trackingService.sendLocation(position);

        setState(() {
          _coordinates = 'Latitude: ${position.latitude}\nLongitude: ${position.longitude}';
          _accuracy = 'Accuracy: ${position.accuracy.toStringAsFixed(1)} m';
          _lastUpdated = DateFormat('HH:mm:ss').format(DateTime.now());
          _status = result ? 'Location sent to backend' : 'Failed to send location';
        });
      },
      onError: (Object error) {
        developer.log('Location stream error: $error');
        setState(() {
          _status = 'Location stream error';
        });
      },
    );
  }

  @override
  void dispose() {
    _positionSubscription?.cancel();
    _apiController.dispose();
    _deviceController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Real-Time Location Tracker'),
        centerTitle: true,
      ),
      body: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Card(
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  children: [
                    TextField(
                      controller: _apiController,
                      decoration: const InputDecoration(
                        labelText: 'Backend URL',
                        hintText: 'http://10.0.2.2:8080',
                      ),
                      onSubmitted: (_) => _trackingService.baseUrl = _apiController.text,
                    ),
                    const SizedBox(height: 12),
                    TextField(
                      controller: _deviceController,
                      decoration: const InputDecoration(
                        labelText: 'Device ID',
                      ),
                      onSubmitted: (_) => _trackingService.deviceId = _deviceController.text,
                    ),
                    const SizedBox(height: 12),
                    ElevatedButton.icon(
                      onPressed: () async {
                        _trackingService.baseUrl = _apiController.text;
                        _trackingService.deviceId = _deviceController.text;
                        if (_isTracking) {
                          await _positionSubscription?.cancel();
                        }
                        await _startLocationTracking();
                      },
                      icon: const Icon(Icons.location_on),
                      label: const Text('Start tracking'),
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 20),
            Card(
              child: Padding(
                padding: const EdgeInsets.all(20),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      'Status: $_status',
                      style: Theme.of(context).textTheme.titleMedium,
                    ),
                    const SizedBox(height: 12),
                    Text(
                      _coordinates,
                      style: const TextStyle(fontSize: 18),
                    ),
                    const SizedBox(height: 12),
                    Text(
                      _accuracy,
                      style: const TextStyle(fontSize: 18),
                    ),
                    const SizedBox(height: 12),
                    Text(
                      'Last update: $_lastUpdated',
                      style: const TextStyle(fontSize: 16),
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
