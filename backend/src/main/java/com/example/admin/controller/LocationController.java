package com.example.admin.controller;

import com.example.admin.model.DeviceLocation;
import com.example.admin.model.LocationUpdateRequest;
import com.example.admin.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping("/update")
    public ResponseEntity<DeviceLocation> updateLocation(@Valid @RequestBody LocationUpdateRequest request) {
        DeviceLocation saved = locationService.saveLocation(request);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/all")
    public ResponseEntity<List<DeviceLocation>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    @GetMapping("/{deviceId}")
    public ResponseEntity<DeviceLocation> getLocationByDevice(@PathVariable String deviceId) {
        return ResponseEntity.ok(locationService.getLocationByDevice(deviceId));
    }
}
