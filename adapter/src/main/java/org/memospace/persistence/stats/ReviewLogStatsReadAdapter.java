package org.memospace.persistence.stats;

import lombok.RequiredArgsConstructor;
import org.memospace.model.stats.DeckScope;
import org.memospace.model.stats.ReviewRow;
import org.memospace.port.stats.ReviewLogStatsReadPort;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Stream;

/**
 * Adapter for reading review log data for statistics.
 */
@Component
@RequiredArgsConstructor
public class ReviewLogStatsReadAdapter implements ReviewLogStatsReadPort {

    private final EntityManager entityManager;

    @Override
    public Stream<ReviewRow> streamReviews(DeckScope scope, Instant fromInclusive, Instant toExclusive) {
        LocalDateTime fromLdt = LocalDateTime.ofInstant(fromInclusive, ZoneId.systemDefault());
        LocalDateTime toLdt = LocalDateTime.ofInstant(toExclusive, ZoneId.systemDefault());

        String queryStr;
        TypedQuery<Object[]> query;

        if (scope.isAll()) {
            queryStr = "SELECT r.cardId, r.deckId, r.reviewedAt, r.quality, " +
                    "r.easeFactorAfter, r.intervalAfter, r.msSpent " +
                    "FROM ReviewLogEntity r " +
                    "WHERE r.reviewedAt >= :from AND r.reviewedAt < :to " +
                    "ORDER BY r.reviewedAt";
            query = entityManager.createQuery(queryStr, Object[].class);
        } else {
            queryStr = "SELECT r.cardId, r.deckId, r.reviewedAt, r.quality, " +
                    "r.easeFactorAfter, r.intervalAfter, r.msSpent " +
                    "FROM ReviewLogEntity r " +
                    "WHERE r.deckId = :deckId AND r.reviewedAt >= :from AND r.reviewedAt < :to " +
                    "ORDER BY r.reviewedAt";
            query = entityManager.createQuery(queryStr, Object[].class);
            query.setParameter("deckId", scope.deckId());
        }

        query.setParameter("from", fromLdt);
        query.setParameter("to", toLdt);

        return query.getResultStream()
                .map(row -> new ReviewRow(
                        (Long) row[0],           // cardId
                        (Long) row[1],           // deckId
                        toInstant((LocalDateTime) row[2]),  // reviewedAt
                        (Integer) row[3],        // quality
                        (Double) row[4],         // easeFactorAfter
                        (Integer) row[5],        // intervalAfter
                        (Integer) row[6]         // msSpent
                ));
    }

    private Instant toInstant(LocalDateTime ldt) {
        return ldt.atZone(ZoneId.systemDefault()).toInstant();
    }
}
