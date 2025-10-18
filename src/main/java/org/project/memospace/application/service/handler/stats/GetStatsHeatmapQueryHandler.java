package org.project.memospace.application.service.handler.stats;

import lombok.RequiredArgsConstructor;
import org.project.memospace.application.service.QueryHandler;
import org.project.memospace.application.service.query.stats.GetStatsHeatmapQuery;
import org.project.memospace.domain.model.stats.*;
import org.project.memospace.domain.port.stats.ReviewLogStatsReadPort;
import org.project.memospace.domain.service.stats.StatsService;
import org.project.memospace.domain.service.stats.StreakService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handler for getting heatmap statistics.
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetStatsHeatmapQueryHandler implements QueryHandler<GetStatsHeatmapQuery, HeatmapStats> {

    private final ReviewLogStatsReadPort reviewLogStatsPort;
    private final StatsService statsService;
    private final StreakService streakService;
    private final Clock clock;

    @Override
    public HeatmapStats handle(GetStatsHeatmapQuery query) {
        // Validate year
        if (query.getYear() < 1970 || query.getYear() > 2100) {
            throw new IllegalArgumentException("Year must be between 1970 and 2100");
        }

        // For now, use default cutoff (UTC, 4 AM)
        Cutoff cutoff = Cutoff.defaultCutoff();

        // Determine deck scope
        DeckScope scope = query.getDeckId() != null
                ? DeckScope.forDeck(query.getDeckId())
                : DeckScope.all();

        // Create window for the year
        LocalDate yearStart = LocalDate.of(query.getYear(), 1, 1);
        LocalDate yearEnd = LocalDate.of(query.getYear(), 12, 31).plusDays(1);
        Window window = new Window(yearStart, yearEnd);

        // Get instant range
        Window.InstantRange range = window.toInstantRange(cutoff);

        // Stream reviews and collect to list
        List<ReviewRow> reviews = reviewLogStatsPort
                .streamReviews(scope, range.fromInclusive(), range.toExclusive())
                .collect(Collectors.toList());

        // Compute heatmap stats
        return statsService.computeHeatmap(reviews, query.getYear(), cutoff, streakService);
    }
}
