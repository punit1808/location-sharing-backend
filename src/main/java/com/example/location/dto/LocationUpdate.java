package com.example.location.dto;

public class LocationUpdate {

    public LocationUpdate() {
    }
    private String userId;
    private double lat;
    private double lng;

    // getters and setters
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public LocationUpdate(String userId, double lat, double lng) {
        this.userId = userId;
        this.lat = lat;
        this.lng = lng;
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
