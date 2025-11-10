package org.project.memospace.application.service.handler.stats;

import lombok.RequiredArgsConstructor;
import org.project.memospace.application.service.QueryHandler;
import org.project.memospace.application.service.query.stats.GetStatsHistogramsQuery;
import org.project.memospace.domain.model.stats.*;
import org.project.memospace.domain.port.stats.CardStatsReadPort;
import org.project.memospace.domain.port.stats.ReviewLogStatsReadPort;
import org.project.memospace.domain.service.stats.ForecastService;
import org.project.memospace.domain.service.stats.StatsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handler for getting histogram statistics.
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetStatsHistogramsQueryHandler implements QueryHandler<GetStatsHistogramsQuery, HistogramStats> {

    private final ReviewLogStatsReadPort reviewLogStatsPort;
    private final CardStatsReadPort cardStatsPort;
    private final StatsService statsService;
    private final ForecastService forecastService;
    private final Clock clock;

    @Override
    public HistogramStats handle(GetStatsHistogramsQuery query) {
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

        // Compute histograms (without forecast yet)
        HistogramStats histogramsWithoutForecast = statsService.computeHistograms(reviews);

        // Compute forecast for next 7 days
        LocalDate today = cutoff.today(clock);
        List<LocalDateTime> dueDates = cardStatsPort.getDueDatesForForecast(scope);

        // Convert LocalDateTime to LocalDate using cutoff
        List<LocalDate> dueDatesLocal = dueDates.stream()
                .map(dt -> cutoff.toLocalDate(dt.atZone(cutoff.timezone()).toInstant()))
                .collect(Collectors.toList());

        List<DuePoint> forecast = forecastService.forecastDueSimple(dueDatesLocal, today, 7);

        // Return complete histogram stats
        return new HistogramStats(
                histogramsWithoutForecast.intervals(),
                histogramsWithoutForecast.ease(),
                histogramsWithoutForecast.answerButtons(),
                forecast
        );
    }

    private Window parseWindow(String windowStr, Cutoff cutoff, Clock clock) {
        LocalDate today = cutoff.today(clock);

        return switch (windowStr) {
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
