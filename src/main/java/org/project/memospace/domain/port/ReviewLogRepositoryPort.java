package org.project.memospace.domain.port;

import org.project.memospace.domain.model.ReviewLog;

import java.time.LocalDateTime;
import java.util.List;

public interface ReviewLogRepositoryPort {
    ReviewLog save(ReviewLog reviewLog);

    List<ReviewLog> findByDeckIdAndDateRange(Long deckId, LocalDateTime from, LocalDateTime to);

    List<ReviewLog> findByDateRange(LocalDateTime from, LocalDateTime to);
}