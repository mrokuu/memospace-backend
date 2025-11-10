package org.project.memospace.application.service.query.stats;

import lombok.Value;
import org.project.memospace.application.service.Query;
import org.project.memospace.domain.model.stats.OverviewStats;

/**
 * Query to get statistics overview for a deck and window.
 */
@Value
public class GetStatsOverviewQuery implements Query<OverviewStats> {
    Long deckId;  // null means "all"
    String window;  // "7d", "30d", "90d", "365d", or "all"
}
