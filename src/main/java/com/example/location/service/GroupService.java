package com.example.location.service;

import com.example.location.entity.GroupEntity;
import com.example.location.repository.GroupRepository;
import org.springframework.stereotype.Service;
import com.example.location.entity.GroupMemberEntity;
import com.example.location.entity.GroupMemberId;
import com.example.location.entity.UserEntity;
import com.example.location.repository.GroupMemberRepository;
import com.example.location.repository.UserRepository;
import com.example.location.cache.LocationCache;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final UserRepository userRepository;
    private final LocationCache locationCache;

    public GroupService(GroupRepository groupRepository, GroupMemberRepository memberRepository, UserRepository userRepository, LocationCache locationCache) {
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
        this.locationCache = locationCache;
    }

    @Transactional
    public GroupEntity createGroup(String name, UUID creatorId) {
        UserEntity creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GroupEntity group = new GroupEntity();
        group.setName(name);
        group.setCreatedBy(creator);

        groupRepository.save(group);

        // ✅ Flush to DB so groupId is available before next query
        groupRepository.flush();
        locationCache.mapGrpToUser(creatorId, group.getId());

        GroupMemberEntity member = new GroupMemberEntity();
        member.setId(new GroupMemberId(group.getId(), creatorId)); // ✅ set composite key
        member.setGroup(group);
        member.setUser(creator);
        member.setRole("ADMIN");

        memberRepository.save(member);

        return group;
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
        locationCache.mapGrpToUser(userId, group.getId());

    }

    public boolean isUserInGroup(UUID groupId, UUID userId) {
        return memberRepository.existsById(new GroupMemberId(userId, groupId));
    }

    public boolean isUserAdmin(UUID groupId, UUID userId) {
        Optional<GroupMemberEntity> memberOpt = memberRepository.findById(new GroupMemberId(userId, groupId));
        return memberOpt.map(member -> "ADMIN".equals(member.getRole())).orElse(false);
    }

    @Transactional
    public boolean deleteGroup(UUID groupId, UUID requesterId) {
        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        GroupMemberId currUser = new GroupMemberId(requesterId, groupId);

        if (!memberRepository.existsById(currUser) || !isUserAdmin(groupId, requesterId)) {
            throw new RuntimeException("Only admins can delete the group");
        }

        // 1️⃣ Delete from DB
        memberRepository.deleteAllByGroupId(groupId);
        groupRepository.delete(group);

        // 2️⃣ Clean up cache
        // remove all members belonging to this group
        locationCache.removeGroup(groupId);

        return true;
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
            locationCache.removeUserFromGroup(userId, groupId); // ✅ also update cache
            return true;
        }

        // Only admins can remove users from the group
        if (!memberRepository.existsById(currUser) || !isUserAdmin(groupId, removedBy)) {
            return false;
        }

        // User not in group
        if (!memberRepository.existsById(userToRemove)) {
            return false;
        }

        // Remove user
        memberRepository.deleteById(userToRemove);
        locationCache.removeUserFromGroup(userId, groupId); // ✅ update cache

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

        // 1️⃣ Try cache first
        UUID cachedGroupId = locationCache.getGroupIdByName(name);
        if (cachedGroupId == null) {
            // 2️⃣ Fetch all groupIds where user is a member
            List<UUID> groupIds = groupRepository.findGroupIdsByUserId(requestingUserId);

            if (groupIds.isEmpty()) {
                throw new RuntimeException("No groups found for user: " + requestingUserId);
            }

            // 3️⃣ Now filter by name (since name is not unique, but user should belong to it)
            cachedGroupId = groupRepository.findByIdInAndName(groupIds, name)
                    .orElseThrow(() -> new RuntimeException("Group with name '" + name + "' not found for user: " + requestingUserId))
                    .getId();
        }

        // 4️⃣ Store in cache (map user -> group)
        locationCache.mapGrpToUser(requestingUserId, cachedGroupId);

        return cachedGroupId;
    }
}
