package com.example.location.repository;

import com.example.location.entity.GroupMemberEntity;
import com.example.location.entity.GroupMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;   // ✅ for @Modifying
import org.springframework.transaction.annotation.Transactional; // ✅ for @Transactional


import java.util.List;
import java.util.UUID;

public interface GroupMemberRepository extends JpaRepository<GroupMemberEntity, GroupMemberId> {
    List<GroupMemberEntity> findByUserId(UUID userId);
    List<GroupMemberEntity> findByGroupId(UUID groupId);

    @Modifying
    @Transactional
    int deleteAllByGroupId(UUID groupId);
}

