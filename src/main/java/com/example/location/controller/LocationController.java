package com.example.location.controller;


import com.example.location.dto.LocationUpdateRequest;
import com.example.location.service.LocationService;
import org.springframework.web.bind.annotation.*;
import com.example.location.entity.GroupEntity;


import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/location")
public class LocationController {


    private final LocationService locationService;


    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping("/update")
    public void updateLocation(@RequestBody LocationUpdateRequest request) {
        locationService.publishLocation(request);
    }

    @GetMapping("/{groupId}")
    public List<?> getUsersLocation(@PathVariable UUID groupId) {
        
        GroupEntity grpEntity = locationService.getGroupById(groupId);  
        return grpEntity != null ? locationService.getLatestLocationsForGroup(grpEntity) : List.of(); 
    }

}