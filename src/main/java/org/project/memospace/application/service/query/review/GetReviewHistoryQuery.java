package org.project.memospace.application.service.query.review;

import lombok.Value;
import org.project.memospace.application.service.Query;
import org.project.memospace.domain.model.ReviewLog;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class GetReviewHistoryQuery implements Query<List<ReviewLog>> {
    Long deckId;
    LocalDateTime from;
    LocalDateTime to;
}