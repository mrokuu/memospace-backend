package org.project.memospace.domain.model.stats;

import lombok.Builder;

import java.time.Instant;
import java.util.Objects;

/**
 * Simplified review data for statistics computations.
 * Contains only the fields needed for stats aggregation.
 */
@Builder(toBuilder = true)
public record ReviewRow(Long cardId, Long deckId, Instant reviewedAt, int quality, Double easeFactorAfter,
                        Integer intervalAfter, Integer msSpent) {
    public ReviewRow(Long cardId, Long deckId, Instant reviewedAt, int quality,
                     Double easeFactorAfter, Integer intervalAfter, Integer msSpent) {
        this.cardId = Objects.requireNonNull(cardId, "CardId cannot be null");
        this.deckId = Objects.requireNonNull(deckId, "DeckId cannot be null");
        this.reviewedAt = Objects.requireNonNull(reviewedAt, "ReviewedAt cannot be null");
        this.quality = quality;
        this.easeFactorAfter = easeFactorAfter;
        this.intervalAfter = intervalAfter;
        this.msSpent = msSpent;
    }

    public boolean isPassed() {
        return quality == 3 || quality == 4;
    }

    public boolean isAnswered() {
        return quality == 0 || quality == 2 || quality == 3 || quality == 4;
    }
}
