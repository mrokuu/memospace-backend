package org.memospace.service.query.stats;

import lombok.Value;
import org.memospace.service.Query;
import org.memospace.model.stats.OverviewStats;

/**
 * Query to get statistics overview for a deck and window.
 */
@Value
public class GetStatsOverviewQuery implements Query<OverviewStats> {
    Long deckId;  // null means "all"
    String window;  // "7d", "30d", "90d", "365d", or "all"
}
