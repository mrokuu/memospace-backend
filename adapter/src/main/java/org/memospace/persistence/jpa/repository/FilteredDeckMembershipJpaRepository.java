package org.memospace.persistence.jpa.repository;

import org.memospace.persistence.jpa.entity.FilteredDeckMembershipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FilteredDeckMembershipJpaRepository extends JpaRepository<FilteredDeckMembershipEntity, FilteredDeckMembershipEntity.FilteredDeckMembershipId> {

    @Modifying
    @Query("DELETE FROM FilteredDeckMembershipEntity f WHERE f.filteredDeckId = :filteredDeckId")
    void deleteAllByFilteredDeckId(@Param("filteredDeckId") UUID filteredDeckId);

    @Modifying
    @Query("DELETE FROM FilteredDeckMembershipEntity f WHERE f.filteredDeckId = :filteredDeckId AND f.cardId = :cardId")
    void deleteByFilteredDeckIdAndCardId(@Param("filteredDeckId") UUID filteredDeckId, @Param("cardId") Long cardId);

    @Query("SELECT COUNT(f) FROM FilteredDeckMembershipEntity f WHERE f.filteredDeckId = :filteredDeckId")
    int countByFilteredDeckId(@Param("filteredDeckId") UUID filteredDeckId);
}
