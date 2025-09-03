package com.example.location.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.location.cache.LocationCache;
import com.example.location.dto.GrpNames;
import com.example.location.entity.GroupEntity;
import com.example.location.entity.GroupMemberEntity;
import com.example.location.entity.GroupMemberId;
import com.example.location.entity.LocationEntity;
import com.example.location.entity.UserEntity;
import com.example.location.repository.GroupMemberRepository;
import com.example.location.repository.GroupRepository;
import com.example.location.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final UserRepository userRepository;
    private final LocationCache locationCache;
    private final LocationService locationService;

    public GroupService(GroupRepository groupRepository, GroupMemberRepository memberRepository, UserRepository userRepository, LocationCache locationCache, LocationService locationService) {
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
        this.locationCache = locationCache;
        this.locationService = locationService;
    }

    @Transactional
    public GroupEntity createGroup(String name, UUID creatorId) {
        UserEntity creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if group with same name already exists for this creator
        boolean exist = groupRepository.existsByNameAndCreatedBy_Id(name, creatorId);
        if (exist) {
            throw new RuntimeException("Same group name already exists");
        }

        // Create group
        GroupEntity group = new GroupEntity();
        group.setName(name);
        group.setCreatedBy(creator);

        groupRepository.saveAndFlush(group); 

        // Add creator as Admin
        GroupMemberEntity member = new GroupMemberEntity();
        member.setId(new GroupMemberId(creatorId, group.getId()));
        member.setGroup(group);
        member.setUser(creator);
        member.setRole("Admin");

        group.getMembers().add(member); 

        return groupRepository.save(group);
    }


    @Transactional
    public void addUserToGroup(UUID creatorId, UUID userId, UUID groupId, String role) {
        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GroupMemberId id = new GroupMemberId(userId, groupId);

        if (memberRepository.existsById(id)) {
            throw new RuntimeException("User already in group");
        }

        GroupMemberEntity member = new GroupMemberEntity();
        member.setId(new GroupMemberId(group.getId(), user.getId())); // ✅ set composite key
        member.setGroup(group);
        member.setUser(user);
        member.setRole(role);

        memberRepository.save(member);
    }

    public boolean isUserInGroup(UUID groupId, UUID userId) {
        return memberRepository.existsById(new GroupMemberId(userId, groupId));
    }

    public boolean isUserAdmin(UUID groupId, UUID userId) {
        Optional<GroupMemberEntity> memberOpt = memberRepository.findById(new GroupMemberId(userId, groupId));
        return memberOpt.map(member -> "Admin".equals(member.getRole())).orElse(false);
    }

    @Transactional
    public boolean deleteGroup(UUID groupId, UUID requesterId) {
        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (!isUserAdmin(groupId, requesterId)) {
            throw new RuntimeException("Only admins can delete the group");
        }
        groupRepository.delete(group);

        return true;
    }

    public boolean isCreator(UUID groupId, UUID userId) {
    GroupEntity group = groupRepository.findById(groupId)
            .orElseThrow(() -> new RuntimeException("Group not found"));

        return group.getCreatedBy().getId().equals(userId);
    }


    @Transactional
    public boolean removeUserFromGroup(UUID removedBy, UUID groupId, UUID userId) {
        groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        GroupMemberId currUser = new GroupMemberId(removedBy, groupId);
        GroupMemberId userToRemove = new GroupMemberId(userId, groupId);

        if (userId.equals(removedBy)) {
            // Self-leave
            memberRepository.deleteById(userToRemove);
            return true;
        }

        // Only admins can remove users from the group and creator cant be removed
        if (!memberRepository.existsById(currUser) || !isUserAdmin(groupId, removedBy) || !isCreator(groupId,userId)) {
            return false;
        }

        // User not in group
        if (!memberRepository.existsById(userToRemove)) {
            return false;
        }

        // Remove user
        memberRepository.deleteById(userToRemove);

        return true;
    }

    public UUID getUserIdByEmail(String email) {
        if(email == null) {
            throw new RuntimeException("Email cannot be null");
        }
        UUID cachedUserId = locationCache.getUserIdByEmail(email);
        if(cachedUserId == null) {
            cachedUserId = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
        }
        locationCache.mapEmailToUserId(email, cachedUserId);
        return cachedUserId;
    }

    public UUID getGroupIdByName(String name, UUID requestingUserId) {
        if (name == null) {
            throw new RuntimeException("Group name cannot be null");
        }

        // 2️⃣ Fetch all groupIds where user is a member
        List<UUID> groupIds = groupRepository.findGroupIdsByUserId(requestingUserId);

        if (groupIds.isEmpty()) {
            throw new RuntimeException("No groups found for user: " + requestingUserId);
        }

        // 3️⃣ Now filter by name (since name is not unique, but user should belong to it)
        UUID cachedGroupId = groupRepository.findByIdInAndName(groupIds, name)
                .orElseThrow(() -> new RuntimeException("Group with name '" + name + "' not found for user: " + requestingUserId))
                .getId();

        return cachedGroupId;
    }

    public List<GrpNames> getGroupsByEmail(String email) {
        UUID userId = getUserIdByEmail(email);
        
        List<UUID> groupIDss = groupRepository.findGroupIdsByUserId(userId);
        List<GroupEntity> groups = groupRepository.findAllById(groupIDss);
        List<GrpNames> groupNames = new ArrayList<>();
        for (GroupEntity group : groups) {
            groupNames.add(new GrpNames(group.getId(), group.getName()));
        }
        return groupNames;
    }

    public List<GroupEntity> getGroupsByEmailEntity(String email) {
        UUID userId = getUserIdByEmail(email);
        
        List<UUID> groupIDss = groupRepository.findGroupIdsByUserId(userId);
        List<GroupEntity> groups = groupRepository.findAllById(groupIDss);
        
        return groups;
    }

    public List<UUID> getGroupIdsByUserId(UUID userId){
        return groupRepository.findGroupIdsByUserId(userId);
    }

    public GroupEntity getGrpById(UUID groupId){
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
    }

    public List<Map<String, Object>> getGrpUsersWithLocations(String email, UUID groupId) {
        GroupEntity groupEntity = getGrpById(groupId);

        List<Map<String, Object>> memberData = new ArrayList<>();
        List<LocationEntity> dbLocations = locationService.getLatestLocationsForGroup(groupEntity);

        for (LocationEntity loc : dbLocations) {
            UUID uid = loc.getUser().getId();
            locationCache.updateLocation(uid, loc);

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("email", loc.getUser().getEmail());
            userInfo.put("latitude", loc.getLatitude());
            userInfo.put("longitude", loc.getLongitude());
            userInfo.put("timestamp", loc.getLastUpdate());
            memberData.add(userInfo);
        }
        
        return memberData;
    }

}
