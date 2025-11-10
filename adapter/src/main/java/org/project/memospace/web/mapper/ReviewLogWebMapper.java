package org.project.memospace.adapter.web.mapper;

import org.project.memospace.adapter.web.dto.ReviewLogDto;
import org.project.memospace.domain.model.ReviewLog;
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