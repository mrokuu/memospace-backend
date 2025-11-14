package org.memospace.persistence.jpa.repository;

import org.memospace.persistence.jpa.entity.FilteredDeckEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface FilteredDeckJpaRepository extends JpaRepository<FilteredDeckEntity, UUID> {

    @Query("SELECT f FROM FilteredDeckEntity f WHERE f.expiresAt IS NOT NULL AND f.expiresAt < :now")
    List<FilteredDeckEntity> findExpired(@Param("now") Instant now);
}
