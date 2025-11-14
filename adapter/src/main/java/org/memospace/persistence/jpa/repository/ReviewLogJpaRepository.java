package org.memospace.persistence.jpa.repository;

import org.memospace.persistence.jpa.entity.ReviewLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReviewLogJpaRepository extends JpaRepository<ReviewLogEntity, Long> {

    List<ReviewLogEntity> findByCardId(Long cardId);

    @Query("SELECT r FROM ReviewLogEntity r " +
            "JOIN CardEntity c ON r.cardId = c.id " +
            "WHERE c.deckId = :deckId AND r.reviewedAt BETWEEN :from AND :to " +
            "ORDER BY r.reviewedAt DESC")
    List<ReviewLogEntity> findByDeckIdAndDateRange(@Param("deckId") Long deckId,
                                                   @Param("from") LocalDateTime from,
                                                   @Param("to") LocalDateTime to);

    @Query("SELECT r FROM ReviewLogEntity r WHERE r.reviewedAt BETWEEN :from AND :to ORDER BY r.reviewedAt DESC")
    List<ReviewLogEntity> findByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}