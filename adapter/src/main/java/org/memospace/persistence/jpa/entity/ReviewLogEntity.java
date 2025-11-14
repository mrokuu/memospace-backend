package org.memospace.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_logs", indexes = {
        @Index(name = "idx_review_logs_card_id", columnList = "card_id"),
        @Index(name = "idx_review_logs_reviewed_at", columnList = "reviewed_at"),
        @Index(name = "idx_review_logs_deck_reviewed", columnList = "deck_id,reviewed_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_id", nullable = false)
    private Long cardId;

    @Column(name = "deck_id", nullable = false)
    private Long deckId;

    @Column(nullable = false)
    private Integer quality;

    @Column(name = "reviewed_at", nullable = false)
    private LocalDateTime reviewedAt;

    @Column(name = "ms_spent")
    private Integer msSpent;

    @Column(name = "ease_factor_after")
    private Double easeFactorAfter;

    @Column(name = "interval_after")
    private Integer intervalAfter;
}