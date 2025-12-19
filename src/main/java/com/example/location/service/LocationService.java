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

    public void publishLocation(LocationUpdateRequest req) {
        producer.send(req);
    }

    public Optional<LocationEntity> getLatestLocation(UUID userId) {
        LocationEntity live = cache.getUserLocation(userId);
        if (live != null) {
            return Optional.of(live);
        }

        return locationRepository.findTopByUserIdOrderByLastUpdateDesc(userId);
    }

    public GroupEntity getGroupById(UUID groupId) {
        return groupRepository.findById(groupId).orElse(null);
    }

    public List<LocationEntity> getLatestLocationsForGroup(GroupEntity group) {
        List<LocationEntity> locations = new ArrayList<>();

        for (GroupMemberEntity member : group.getMembers()) {
            UUID userId = member.getUser().getId();

            getLatestLocation(userId).ifPresent(locations::add);
        }

        return locations;
    }


}
