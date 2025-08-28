package com.example.location.repository;

import com.example.location.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.time.Instant;
import java.util.UUID;

public interface LocationRepository extends JpaRepository<LocationEntity, UUID> {

    // Queries you already have
    List<LocationEntity> findByUserId(UUID userId);
    Optional<LocationEntity> findTopByUserIdOrderByLastUpdateDesc(UUID userId);

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO locations (user_id, latitude, longitude, last_update)
        VALUES (:userId, :lat, :lng, :lastUpdate)
        ON CONFLICT (user_id) DO UPDATE
        SET latitude = EXCLUDED.latitude,
            longitude = EXCLUDED.longitude,
            last_update = EXCLUDED.last_update
        """, nativeQuery = true)
    void upsert(UUID userId, Double lat, Double lng, Instant lastUpdate);
}
