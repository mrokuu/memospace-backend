package org.project.memospace.application.service.handler.review;

import lombok.RequiredArgsConstructor;
import org.project.memospace.application.service.QueryHandler;
import org.project.memospace.application.service.query.review.GetReviewHistoryQuery;
import org.project.memospace.domain.model.ReviewLog;
import org.project.memospace.domain.port.ReviewLogRepositoryPort;
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