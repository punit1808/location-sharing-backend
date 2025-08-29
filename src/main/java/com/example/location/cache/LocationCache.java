package com.example.location.cache;

import com.example.location.entity.LocationEntity;
import org.springframework.stereotype.Component;    
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LocationCache {
    // UserId -> Latest Location
    private final Map<UUID, LocationEntity> latestLocations = new ConcurrentHashMap<>();

    // Email -> UserId
    private final Map<String, UUID> emailToUserId = new ConcurrentHashMap<>();

    // UserId -> List of GroupIds
    private final Map<UUID, List<UUID>> groupMembersCache = new ConcurrentHashMap<>();

    public void updateLocation(UUID userId, LocationEntity location) {
        latestLocations.put(userId, location);
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

    public void mapGrpToUser(UUID userId, UUID grpId){
    if (grpId == null) {
        groupMembersCache.put(userId, new ArrayList<>());
    } else {
        groupMembersCache.put(userId, new ArrayList<>(List.of(grpId)));
    }
}


    public List<UUID> getGrpByUser(UUID userId){
        return groupMembersCache.get(userId);
    }

    public UUID getGroupIdByName(String name){
        for (Map.Entry<UUID, List<UUID>> entry : groupMembersCache.entrySet()) {
            if (entry.getValue().contains(name)) {
                return entry.getKey();
            }
        }
        return null; 
    }

    public void removeGroup(UUID groupId) {
        groupMembersCache.forEach((userId, groups) -> {
            groups.remove(groupId);
        });
        // also clean up empty lists if needed
        groupMembersCache.entrySet().removeIf(e -> e.getValue().isEmpty());
    }

    public void removeUserFromGroup(UUID userId, UUID groupId) {
        List<UUID> groups = groupMembersCache.get(userId);
        if (groups != null) {
            groups.remove(groupId);
            if (groups.isEmpty()) {
                groupMembersCache.remove(userId); // clean up if no groups left
            }
        }
    }
}

