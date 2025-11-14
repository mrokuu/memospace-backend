package org.memospace.service.handler.review;

import lombok.RequiredArgsConstructor;
import org.memospace.service.QueryHandler;
import org.memospace.service.query.review.GetReviewHistoryQuery;
import org.memospace.model.ReviewLog;
import org.memospace.port.ReviewLogRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetReviewHistoryQueryHandler implements QueryHandler<GetReviewHistoryQuery, List<ReviewLog>> {

    private final ReviewLogRepositoryPort reviewLogRepository;

    @Override
    public List<ReviewLog> handle(GetReviewHistoryQuery query) {
        return reviewLogRepository.findByDeckIdAndDateRange(
                query.getDeckId(),
                query.getFrom(),
                query.getTo()
        );
    }
}