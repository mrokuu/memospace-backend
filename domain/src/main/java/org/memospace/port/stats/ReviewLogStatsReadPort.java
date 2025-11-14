package org.memospace.port.stats;

import org.memospace.model.stats.DeckScope;
import org.memospace.model.stats.ReviewRow;

import java.time.Instant;
import java.util.stream.Stream;

/**
 * Read port for review log data optimized for statistics queries.
 */
public interface ReviewLogStatsReadPort {

    /**
     * Stream review rows for a given deck scope and time range.
     * Returns minimal data needed for stats computations.
     *
     * @param scope Deck scope (specific deck or all)
     * @param fromInclusive Start of time range (inclusive)
     * @param toExclusive End of time range (exclusive)
     * @return Stream of review rows
     */
    Stream<ReviewRow> streamReviews(DeckScope scope, Instant fromInclusive, Instant toExclusive);
}
