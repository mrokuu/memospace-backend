package org.memospace.service.query.review;

import lombok.Value;
import org.memospace.service.Query;
import org.memospace.model.ReviewLog;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class GetReviewHistoryQuery implements Query<List<ReviewLog>> {
    Long deckId;
    LocalDateTime from;
    LocalDateTime to;
}