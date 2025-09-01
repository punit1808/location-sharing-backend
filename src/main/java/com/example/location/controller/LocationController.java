package com.example.location.controller;


import com.example.location.dto.LocationUpdateRequest;
import com.example.location.service.LocationService;

import org.springframework.web.bind.annotation.*;
import com.example.location.dto.LocationUpdate;
import com.example.location.service.GroupService;



import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/location")
public class LocationController {


    private final LocationService locationService;
    private final GroupService groupService;


    public LocationController(LocationService locationService, GroupService groupService) {
        this.locationService = locationService;
        this.groupService = groupService;
    }

    @PostMapping("/update")
    public void updateLocation(@RequestBody LocationUpdate request) {
        UUID userId = groupService.getUserIdByEmail(request.getUserId());
        LocationUpdateRequest locationUpdateRequest = new LocationUpdateRequest(userId, request.getLat(), request.getLng());
        locationService.publishLocation(locationUpdateRequest);
    }

    @GetMapping("/{email}/{groupId}")
    public List<Map<String, Object>> getGrpData(@PathVariable("email") String email, @PathVariable("groupId") UUID groupId) {
        return groupService.getGrpUsersWithLocations(email, groupId);
    }


}