package org.memospace.persistence.jpa.repository;

import org.memospace.persistence.jpa.entity.MediaAssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface MediaAssetJpaRepository extends JpaRepository<MediaAssetEntity, String> {

    @Modifying
    @Query("UPDATE MediaAssetEntity m SET m.usageCount = m.usageCount + :delta WHERE m.id = :id")
    void incrementUsageCount(@Param("id") String id, @Param("delta") int delta);

    @Query("SELECT m FROM MediaAssetEntity m WHERE m.id NOT IN :referencedIds")
    List<MediaAssetEntity> findUnreferenced(@Param("referencedIds") Set<String> referencedIds);

    @Query("SELECT m FROM MediaAssetEntity m")
    List<MediaAssetEntity> findAllAssets();
}
