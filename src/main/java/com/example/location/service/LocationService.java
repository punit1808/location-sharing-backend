package com.example.location.service;

import com.example.location.cache.LocationCache;
import com.example.location.dto.LocationUpdateRequest;
import com.example.location.entity.GroupEntity;
import com.example.location.entity.GroupMemberEntity;
import com.example.location.entity.LocationEntity;
import com.example.location.kafka.LocationProducer;
import com.example.location.repository.GroupRepository;
import com.example.location.repository.LocationRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class LocationService {

    private final LocationCache cache;
    private final LocationRepository locationRepository;
    private final GroupRepository groupRepository;
    private final LocationProducer producer;

    public LocationService(LocationCache cache,
                           LocationRepository locationRepository,
                           GroupRepository groupRepository,
                           LocationProducer producer) {
        this.cache = cache;
        this.locationRepository = locationRepository;
        this.groupRepository = groupRepository;
        this.producer = producer;
    }

    // 1️⃣ Publish location update to Kafka --> coming from controller
    public void publishLocation(LocationUpdateRequest req) {
        producer.send(req);
    }

    // 2️⃣ Get latest location of a single user (cache → DB fallback)
    public Optional<LocationEntity> getLatestLocation(UUID userId) {
    // First check live cache
        LocationEntity live = cache.getUserLocation(userId);
        if (live != null) {
            return Optional.of(live);
        }

        // Fallback to DB
        return locationRepository.findTopByUserIdOrderByLastUpdateDesc(userId);
    }

    public GroupEntity getGroupById(UUID groupId) {
        return groupRepository.findById(groupId).orElse(null);
    }


    // 3️⃣ Get latest locations of all users in a group
    public List<LocationEntity> getLatestLocationsForGroup(GroupEntity group) {
        List<LocationEntity> locations = new ArrayList<>();

        for (GroupMemberEntity member : group.getMembers()) {
            UUID userId = member.getUser().getId();

            getLatestLocation(userId).ifPresent(locations::add);
        }

        return locations;
    }

}
