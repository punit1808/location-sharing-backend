package com.example.location.controller;

import com.example.location.entity.GroupEntity;
import com.example.location.service.GroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import com.example.location.dto.CreateGrp;
import com.example.location.dto.AddUser;

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
    public ResponseEntity<String> deleteGroup(@RequestParam String userId,@RequestParam String groupId) {
        groupService.deleteGroup(java.util.UUID.fromString(groupId),java.util.UUID.fromString(userId));
        return ResponseEntity.ok("Group deleted");
    }

    @DeleteMapping("/removeUser")
    public ResponseEntity<String> removeUserFromGroup(@RequestParam String removedBy,@RequestParam String groupId, @RequestParam String userId) {
        groupService.removeUserFromGroup(java.util.UUID.fromString(removedBy),java.util.UUID.fromString(groupId), java.util.UUID.fromString(userId));
        return ResponseEntity.ok("User removed from group");
    }

}
