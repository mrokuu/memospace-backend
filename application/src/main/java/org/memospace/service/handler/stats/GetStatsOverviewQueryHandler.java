package org.memospace.service.handler.stats;

import lombok.RequiredArgsConstructor;
import org.memospace.service.QueryHandler;
import org.memospace.service.query.stats.GetStatsOverviewQuery;
import org.memospace.model.stats.*;
import org.memospace.port.stats.ReviewLogStatsReadPort;
import org.memospace.service.stats.StatsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handler for getting statistics overview.
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetStatsOverviewQueryHandler implements QueryHandler<GetStatsOverviewQuery, OverviewStats> {

    private final ReviewLogStatsReadPort reviewLogStatsPort;
    private final StatsService statsService;
    private final Clock clock;

    @Override
    public OverviewStats handle(GetStatsOverviewQuery query) {
        // For now, use default cutoff (UTC, 4 AM)
        Cutoff cutoff = Cutoff.defaultCutoff();

        // Parse window
        Window window = parseWindow(query.getWindow(), cutoff, clock);

        // Determine deck scope
        DeckScope scope = query.getDeckId() != null
                ? DeckScope.forDeck(query.getDeckId())
                : DeckScope.all();

        // Get instant range
        Window.InstantRange range = window.toInstantRange(cutoff);

        // Stream reviews and collect to list
        List<ReviewRow> reviews = reviewLogStatsPort
                .streamReviews(scope, range.fromInclusive(), range.toExclusive())
                .collect(Collectors.toList());

        // Compute overview stats
        return statsService.computeOverview(reviews, window, cutoff);
    }

    private Window parseWindow(String windowStr, Cutoff cutoff, Clock clock) {
        LocalDate today = cutoff.today(clock);

        return switch (windowStr) {
            case "7d" -> Window.fromDays(7, cutoff, clock);
            case "30d" -> Window.fromDays(30, cutoff, clock);
            case "90d" -> Window.fromDays(90, cutoff, clock);
            case "365d" -> Window.fromDays(365, cutoff, clock);
            case "all" -> {
                // For "all", use a reasonable earliest date (e.g., 10 years ago)
                LocalDate earliest = today.minusYears(10);
                yield Window.all(earliest, cutoff, clock);
            }
            default -> throw new IllegalArgumentException("Invalid window: " + windowStr);
        };
    }
}
