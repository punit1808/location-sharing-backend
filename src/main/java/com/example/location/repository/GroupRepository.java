// repository/GroupRepository.java
package com.example.location.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;
import java.util.List;
import java.util.Optional;
import com.example.location.entity.GroupEntity;

public interface GroupRepository extends JpaRepository<GroupEntity, UUID> {
    
    @Query("SELECT g.id FROM GroupEntity g JOIN g.members m WHERE m.user.id = :userId")
    List<UUID> findGroupIdsByUserId(@Param("userId") UUID userId);

    // New one to match both groupIds and name
    Optional<GroupEntity> findByIdInAndName(List<UUID> groupIds, String name);

}