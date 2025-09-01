package com.example.location.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    // UserId -> List of GroupIds
    private final Map<UUID, List<UUID>> groupMembersCache = new ConcurrentHashMap<>();

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

