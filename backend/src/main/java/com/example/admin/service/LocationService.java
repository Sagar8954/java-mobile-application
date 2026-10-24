package com.example.admin.service;

import com.example.admin.model.DeviceLocation;
import com.example.admin.model.LocationUpdateRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LocationService {

    private final Map<String, DeviceLocation> latestLocations = new ConcurrentHashMap<>();

    public DeviceLocation saveLocation(LocationUpdateRequest request) {
        DeviceLocation location = new DeviceLocation(
                request.getDeviceId(),
                request.getLatitude(),
                request.getLongitude(),
                request.getAccuracyMeters(),
                System.currentTimeMillis()
        );

        latestLocations.put(request.getDeviceId(), location);
        return location;
    }

    public List<DeviceLocation> getAllLocations() {
        return new ArrayList<>(latestLocations.values());
    }

    public DeviceLocation getLocationByDevice(String deviceId) {
        DeviceLocation location = latestLocations.get(deviceId);
        if (location == null) {
            throw new IllegalArgumentException("Device not found: " + deviceId);
        }
        return location;
    }
}
