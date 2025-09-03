package com.example.location.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;


import com.example.location.entity.LocationEntity; 

@Component
public class LocationCache {

    // UserId -> Latest Location
    private final Map<UUID, LocationEntity> latestLocations = new ConcurrentHashMap<>();

    // Email -> UserId
    private final Map<String, UUID> emailToUserId = new ConcurrentHashMap<>();

    // Set of UserIds with unsaved location changes
    private final Set<UUID> dirtyUsers = ConcurrentHashMap.newKeySet();

    public void updateLocation(UUID userId, LocationEntity location) {
        latestLocations.put(userId, location);
        dirtyUsers.add(userId); // mark as dirty
    }

    public LocationEntity getUserLocation(UUID userId) {
        return latestLocations.get(userId);
    }

    public Map<UUID, LocationEntity> getAllLatestLocations() {
        return new HashMap<>(latestLocations);
    }

    public void mapEmailToUserId(String email, UUID userId) {
        emailToUserId.put(email, userId);
    }

    public UUID getUserIdByEmail(String email) {
        return emailToUserId.get(email);
    }

    // New method: get dirty locations only
    public Map<UUID, LocationEntity> getDirtyLocations() {
        Map<UUID, LocationEntity> result = new HashMap<>();
        for (UUID userId : dirtyUsers) {
            LocationEntity loc = latestLocations.get(userId);
            if (loc != null) {
                result.put(userId, loc);
            }
        }
        return result;
    }

    // New method: clear flushed users
    public void clearDirty(Set<UUID> flushed) {
        dirtyUsers.removeAll(flushed);
    }
}

