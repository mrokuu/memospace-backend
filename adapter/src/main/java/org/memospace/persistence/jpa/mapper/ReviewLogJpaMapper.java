package org.memospace.persistence.jpa.mapper;

import org.memospace.persistence.jpa.entity.ReviewLogEntity;
import org.memospace.model.ReviewLog;
import org.springframework.stereotype.Component;

@Component
public class ReviewLogJpaMapper {

    public ReviewLogEntity toEntity(ReviewLog reviewLog) {
        return new ReviewLogEntity(
                reviewLog.id(),
                reviewLog.cardId(),
                reviewLog.deckId(),
                reviewLog.quality(),
                reviewLog.reviewedAt(),
                reviewLog.msSpent(),
                reviewLog.easeFactorAfter(),
                reviewLog.intervalAfter()
        );
    }

    public ReviewLog toDomain(ReviewLogEntity entity) {
        return new ReviewLog(
                entity.getId(),
                entity.getCardId(),
                entity.getDeckId(),
                entity.getQuality(),
                entity.getReviewedAt(),
                entity.getMsSpent(),
                entity.getEaseFactorAfter(),
                entity.getIntervalAfter()
        );
    }
}