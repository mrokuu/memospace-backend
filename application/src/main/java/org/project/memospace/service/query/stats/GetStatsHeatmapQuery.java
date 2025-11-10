package org.project.memospace.application.service.query.stats;

import lombok.Value;
import org.project.memospace.application.service.Query;
import org.project.memospace.domain.model.stats.HeatmapStats;

/**
 * Query to get heatmap statistics for a year.
 */
@Value
public class GetStatsHeatmapQuery implements Query<HeatmapStats> {
    Long deckId;  // null means "all"
    int year;
}
