// repository/GroupRepository.java
package com.example.location.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;
import java.util.List;
import java.util.Optional;
import com.example.location.entity.GroupEntity;
import org.springframework.data.jpa.repository.Modifying;   // ✅ for @Modifying
import org.springframework.transaction.annotation.Transactional; // ✅ for @Transactional

public interface GroupRepository extends JpaRepository<GroupEntity, UUID> {
    @Query("SELECT g.id FROM GroupEntity g JOIN g.members m WHERE m.user.id = :userId")
    List<UUID> findGroupIdsByUserId(@Param("userId") UUID userId);

    Optional<GroupEntity> findByIdInAndName(List<UUID> groupIds, String name);

    boolean existsByNameAndCreatedBy_Id(String name, UUID userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM GroupEntity g WHERE g.id = :groupId")
    int deleteByIdCustom(@Param("groupId") UUID groupId);
}
