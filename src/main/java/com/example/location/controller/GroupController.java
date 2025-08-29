package com.example.location.controller;

import com.example.location.entity.GroupEntity;
import com.example.location.service.GroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import com.example.location.dto.CreateGrp;
import com.example.location.dto.AddUser;
import com.example.location.dto.DeleteGrp;

// from here we can create groups and get users in a group

@CrossOrigin("*")
@RestController
@RequestMapping("/group")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping("/create")
    public ResponseEntity<GroupEntity> createGroup(@RequestBody CreateGrp createGrp) {
        UUID creatorId = groupService.getUserIdByEmail(createGrp.getEmail());
        String name = createGrp.getName();
        GroupEntity group = groupService.createGroup(name,creatorId);
        return ResponseEntity.ok(group);
    }

    @PostMapping("/addUser")
    public ResponseEntity<String> addUserToGroup(@RequestBody AddUser addUs){
        UUID addedBy = groupService.getUserIdByEmail(addUs.getAddedBy());
        UUID userId = groupService.getUserIdByEmail(addUs.getUserId());
        UUID groupId = groupService.getGroupIdByName(addUs.getGroupId(), addedBy);
        String role = addUs.getRole();

        groupService.addUserToGroup(addedBy,userId,groupId,role);
        return ResponseEntity.ok("User added to group");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteGroup(@RequestBody DeleteGrp delGrp) {
        UUID userId = groupService.getUserIdByEmail(delGrp.getUserId());
        UUID groupId = groupService.getGroupIdByName(delGrp.getGroupId(), userId);
        Boolean ck=groupService.deleteGroup(groupId,userId);
        if(!ck) {
            return ResponseEntity.status(403).body("Only ADMIN can delete group");
        }
        return ResponseEntity.ok("Group deleted");
    }

    @DeleteMapping("/removeUser")
    public ResponseEntity<String> removeUserFromGroup(@RequestBody DeleteGrp delGrp) {
        UUID userId = groupService.getUserIdByEmail(delGrp.getUserId());
        UUID groupId = groupService.getGroupIdByName(delGrp.getGroupId(), userId);
        UUID removedBy = groupService.getUserIdByEmail(delGrp.getRemovedBy());
        Boolean ck=groupService.removeUserFromGroup(removedBy,groupId, userId);
        if(!ck) {
            return ResponseEntity.status(403).body("Only ADMIN can remove users");
        }
        return ResponseEntity.ok("User removed from group");
    }

}
