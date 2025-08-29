// repository/UserRepository.java
package com.example.location.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import com.example.location.entity.UserEntity;
import java.util.Optional;


public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);
}