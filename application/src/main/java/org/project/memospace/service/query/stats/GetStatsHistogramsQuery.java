package org.project.memospace.application.service.query.stats;

import lombok.Value;
import org.project.memospace.application.service.Query;
import org.project.memospace.domain.model.stats.HistogramStats;

/**
 * Query to get histogram statistics (intervals, ease, answer buttons, forecast).
 */
@Value
public class GetStatsHistogramsQuery implements Query<HistogramStats> {
    Long deckId;  // null means "all"
    String window;  // "30d", "90d", "365d", or "all"
}
