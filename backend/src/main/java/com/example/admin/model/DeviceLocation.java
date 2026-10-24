package com.example.admin.model;

public class DeviceLocation {
    private String deviceId;
    private double latitude;
    private double longitude;
    private double accuracyMeters;
    private long timestamp;

    public DeviceLocation() {
    }

    public DeviceLocation(String deviceId, double latitude, double longitude, double accuracyMeters, long timestamp) {
        this.deviceId = deviceId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracyMeters = accuracyMeters;
        this.timestamp = timestamp;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAccuracyMeters() {
        return accuracyMeters;
    }

    public void setAccuracyMeters(double accuracyMeters) {
        this.accuracyMeters = accuracyMeters;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
