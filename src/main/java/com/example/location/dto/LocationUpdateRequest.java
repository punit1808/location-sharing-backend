package com.example.location.dto;
import java.util.UUID;

// Send location updates from controller to location service

public class LocationUpdateRequest {
    private UUID userId;
    private double lat;
    
    public LocationUpdateRequest(UUID userId, double lat, double lng) {
        this.userId = userId;
        this.lat = lat;
        this.lng = lng;
    }
    public LocationUpdateRequest() {
    }
    private double lng;

    // getters and setters
    public UUID getUserId() {
        return userId;
    }
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    public double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public double getLng() {
        return lng;
    }
    public void setLng(double lng) {
        this.lng = lng;
    }
}
