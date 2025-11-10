package org.project.memospace.domain.model;

import lombok.Builder;
import lombok.With;

import java.time.LocalDateTime;
import java.util.Objects;

@Builder(toBuilder = true)
public record ReviewLog(@With Long id, Long cardId, Long deckId, int quality, LocalDateTime reviewedAt, Integer msSpent,
                        Double easeFactorAfter, Integer intervalAfter) {
    public ReviewLog(Long id, Long cardId, Long deckId, int quality, LocalDateTime reviewedAt,
                     Integer msSpent, Double easeFactorAfter, Integer intervalAfter) {
        this.id = id;
        this.cardId = Objects.requireNonNull(cardId, "CardId cannot be null");
        this.deckId = deckId;
        this.quality = quality;
        this.reviewedAt = Objects.requireNonNull(reviewedAt, "ReviewedAt cannot be null");
        this.msSpent = msSpent;
        this.easeFactorAfter = easeFactorAfter;
        this.intervalAfter = intervalAfter;
    }

    public static ReviewLog create(Long cardId, Long deckId, ReviewResult reviewResult,
                                   Double easeFactorAfter, Integer intervalAfter) {
        return new ReviewLog(null, cardId, deckId, reviewResult.quality(),
                reviewResult.reviewedAt(), reviewResult.msSpent(),
                easeFactorAfter, intervalAfter);
    }

}