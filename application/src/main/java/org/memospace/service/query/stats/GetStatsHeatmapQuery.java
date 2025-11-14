package org.memospace.service.query.stats;

import lombok.Value;
import org.memospace.service.Query;
import org.memospace.model.stats.HeatmapStats;

/**
 * Query to get heatmap statistics for a year.
 */
@Value
public class GetStatsHeatmapQuery implements Query<HeatmapStats> {
    Long deckId;  // null means "all"
    int year;
}
