package org.project.memospace.adapter.persistence.jpa.repository;

import org.project.memospace.adapter.persistence.jpa.entity.CardEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CardJpaRepository extends JpaRepository<CardEntity, Long> {

    List<CardEntity> findByDeckId(Long deckId);

    void deleteByDeckId(Long deckId);

    @Query("SELECT c FROM CardEntity c WHERE c.dueAt <= :now AND c.deckId = :deckId ORDER BY c.dueAt ASC")
    List<CardEntity> findDueCardsByDeckId(@Param("deckId") Long deckId, @Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT c FROM CardEntity c WHERE c.dueAt <= :now ORDER BY c.dueAt ASC")
    List<CardEntity> findDueCards(@Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT c FROM CardEntity c WHERE " +
            "(:deckId IS NULL OR c.deckId = :deckId) AND " +
            "(:searchTerm IS NULL OR LOWER(c.front) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(c.back) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(c.tags) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
            "(:onlyDue = false OR c.dueAt <= :now)")
    List<CardEntity> searchCards(@Param("deckId") Long deckId,
                                 @Param("searchTerm") String searchTerm,
                                 @Param("onlyDue") Boolean onlyDue,
                                 @Param("now") LocalDateTime now,
                                 Pageable pageable);

    void deleteAllByNoteId(UUID noteId);

    List<CardEntity> findAllByNoteId(UUID noteId);
}