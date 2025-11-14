package org.memospace.web.mapper;

import org.memospace.web.dto.ReviewLogDto;
import org.memospace.model.ReviewLog;
import org.springframework.stereotype.Component;

@Component
public class ReviewLogWebMapper {

    public ReviewLogDto toDto(ReviewLog reviewLog) {
        return new ReviewLogDto(
                reviewLog.id(),
                reviewLog.cardId(),
                reviewLog.quality(),
                reviewLog.reviewedAt(),
                reviewLog.msSpent()
        );
    }
}