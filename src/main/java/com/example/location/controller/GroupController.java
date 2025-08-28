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

@RestController
@RequestMapping("/group")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping("/create")
    public ResponseEntity<GroupEntity> createGroup(@RequestBody CreateGrp createGrp) {
        UUID creatorId = createGrp.getCreatorId();
        String name = createGrp.getName();
        GroupEntity group = groupService.createGroup(name,creatorId);
        return ResponseEntity.ok(group);
    }

    @PostMapping("/addUser")
    public ResponseEntity<String> addUserToGroup(@RequestBody AddUser addUs){
        UUID addedBy = addUs.getAddedBy();
        UUID groupId = addUs.getGroupId();
        UUID userId = addUs.getUserId();
        String role = addUs.getRole();

        groupService.addUserToGroup(addedBy,userId,groupId,role);
        return ResponseEntity.ok("User added to group");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteGroup(@RequestBody DeleteGrp delGrp) {
        groupService.deleteGroup(delGrp.getGroupId(),delGrp.getUserId());
        return ResponseEntity.ok("Group deleted");
    }

    @DeleteMapping("/removeUser")
    public ResponseEntity<String> removeUserFromGroup(@RequestBody DeleteGrp delGrp) {
        groupService.removeUserFromGroup(delGrp.getRemovedBy(),delGrp.getGroupId(), delGrp.getUserId());
        return ResponseEntity.ok("User removed from group");
    }

}
