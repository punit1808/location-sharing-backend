package com.example.location.kafka;

import com.example.location.entity.LocationEntity;

import java.time.Instant;
import java.util.*;

import com.example.location.WebSocket.LocationWebSocketHandler;
import com.example.location.cache.LocationCache;
import com.example.location.dto.LocationUpdateRequest;  
import com.example.location.entity.UserEntity;
import com.example.location.repository.LocationRepository;
import com.example.location.repository.UserRepository;
import com.example.location.service.GroupService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class LocationConsumer {

    private final LocationCache cache;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    public LocationConsumer(LocationCache cache,
                            LocationRepository locationRepository,
                            UserRepository userRepository) {
        this.cache = cache;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
    }
    @Autowired
    private LocationWebSocketHandler wsHandler;
    @Autowired
    private GroupService groupService;

    public void updateUserLocation(UUID userId, LocationEntity location) {
        // Save/update in DB here ...
        List<UUID> Grps=groupService.getGroupIdsByUserId(userId);
        
        for(UUID GrpId : Grps){
            wsHandler.broadcastLocation(GrpId.toString(), location);
        }
    }

    @KafkaListener(topics = "location-updates", groupId = "location-service")
    public void consume(LocationUpdateRequest req) {
        UserEntity user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + req.getUserId()));

        LocationEntity location = new LocationEntity();
        location.setUser(user);
        location.setLatitude(req.getLat());
        location.setLongitude(req.getLng());
        location.setLastUpdate(Instant.now());

        // update live cache
        cache.updateLocation(req.getUserId(), location);
        updateUserLocation(req.getUserId(),location);
    }

    // flush latest per user every 5 sec
    @Scheduled(fixedRate = 5000)
    @Transactional
    public void flushToDb() {
        Map<UUID, LocationEntity> dirty = cache.getDirtyLocations();

        dirty.forEach((userId, loc) -> {
            locationRepository.upsert(
                userId,
                loc.getLatitude(),
                loc.getLongitude(),
                loc.getLastUpdate()
            );
        });

        cache.clearDirty(dirty.keySet()); // clear dirty flags after flush
    }

}

