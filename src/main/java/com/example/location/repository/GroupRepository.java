// repository/GroupRepository.java
package com.example.location.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import com.example.location.entity.GroupEntity;

public interface GroupRepository extends JpaRepository<GroupEntity, UUID> {

    boolean existsById(UUID id);
}