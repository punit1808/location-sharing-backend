package com.example.location.service;

import com.example.location.entity.GroupEntity;
import com.example.location.repository.GroupRepository;
import org.springframework.stereotype.Service;
import com.example.location.entity.GroupMemberEntity;
import com.example.location.entity.GroupMemberId;
import com.example.location.entity.UserEntity;
import com.example.location.repository.GroupMemberRepository;
import com.example.location.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final UserRepository userRepository;

    public GroupService(GroupRepository groupRepository, GroupMemberRepository memberRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
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
        member.setRole("ADMIN");

        memberRepository.save(member);

    }

    public boolean isUserInGroup(UUID groupId, UUID userId) {
        return memberRepository.existsById(new GroupMemberId(userId, groupId));
    }

    public boolean isUserAdmin(UUID groupId, UUID userId) {
        Optional<GroupMemberEntity> memberOpt = memberRepository.findById(new GroupMemberId(userId, groupId));
        return memberOpt.map(member -> "ADMIN".equals(member.getRole())).orElse(false);
    }

    public boolean deleteGroup(UUID groupId, UUID requesterId) {
        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        GroupMemberId currUser = new GroupMemberId(requesterId, groupId);
        
        if(!memberRepository.existsById(currUser) || !isUserAdmin(groupId, requesterId)) {
            throw new RuntimeException("Only admins can delete the group");
        }

        memberRepository.deleteAllByGroupId(groupId);
        groupRepository.delete(group);
        return true;
    }

    public boolean removeUserFromGroup( UUID removedBy,UUID groupId, UUID userId) {
        groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        
        GroupMemberId currUser = new GroupMemberId(removedBy, groupId);
        GroupMemberId userToRemove = new GroupMemberId(userId, groupId);

        if(userId.equals(removedBy)) {
            memberRepository.deleteById(userToRemove);
            return true;
        }

        if(!memberRepository.existsById(currUser) || !isUserAdmin(groupId, removedBy)) {
            return false;
            // throw new RuntimeException("Only admins can remove users from the group");
        }

        if(!memberRepository.existsById(userToRemove)) {
            return false;
            // throw new RuntimeException("User not in group");
        }

        memberRepository.deleteById(userToRemove);
        return true;
    }
}
