package com.example.location.cache;

import com.example.location.entity.LocationEntity;
import org.springframework.stereotype.Component;    
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LocationCache {
    private final Map<UUID, LocationEntity> latestLocations = new ConcurrentHashMap<>();

    public void updateLocation(UUID userId, LocationEntity location) {
        latestLocations.put(userId, location);
    }

    public LocationEntity getUserLocation(UUID userId) {
        return latestLocations.get(userId);
    }

    public Map<UUID, LocationEntity> getAllLatestLocations() {
        return new HashMap<>(latestLocations);
    }
}

