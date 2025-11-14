package org.memospace.web.mapper;

import org.memospace.web.dto.stats.*;
import org.memospace.model.stats.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper for statistics web DTOs.
 */
@Component
public class StatsWebMapper {

    public StatsOverviewDto toOverviewDto(OverviewStats stats, String deckIdParam, String window, Cutoff cutoff, Window windowObj) {
        return new StatsOverviewDto(
                deckIdParam != null ? deckIdParam.toString() : "all",
                window,
                new StatsOverviewDto.DateRangeDto(
                        windowObj.fromInclusive(),
                        windowObj.toExclusive().minusDays(1), // Spec shows inclusive end
                        cutoff.timezone().getId(),
                        cutoff.cutoffHour()
                ),
                new StatsOverviewDto.SeriesDto(
                        stats.reviewsPerDay().stream()
                                .map(r -> new StatsOverviewDto.ReviewsPerDayDto(r.date(), r.count()))
                                .collect(Collectors.toList()),
                        stats.retentionPerDay().stream()
                                .map(r -> new StatsOverviewDto.RetentionPerDayDto(r.getDate(), r.getPassed(), r.getTotal(), r.getRatio()))
                                .collect(Collectors.toList()),
                        stats.answerButtonsPerDay().stream()
                                .map(a -> new StatsOverviewDto.AnswerButtonsPerDayDto(a.date(), a.again(), a.hard(), a.good(), a.easy()))
                                .collect(Collectors.toList()),
                        stats.timeSpentPerDay().stream()
                                .map(t -> new StatsOverviewDto.TimeSpentPerDayDto(t.date(), t.milliseconds()))
                                .collect(Collectors.toList())
                ),
                new StatsOverviewDto.TotalsDto(
                        stats.totals().getReviews(),
                        stats.totals().getPassed(),
                        stats.totals().getRetentionRatio(),
                        stats.totals().getTimeSpentMs()
                )
        );
    }

    public StatsHeatmapDto toHeatmapDto(HeatmapStats stats, Cutoff cutoff) {
        return new StatsHeatmapDto(
                stats.year(),
                cutoff.timezone().getId(),
                cutoff.cutoffHour(),
                stats.days().stream()
                        .map(d -> new StatsHeatmapDto.DayCountDto(d.getDate(), d.getCount()))
                        .collect(Collectors.toList()),
                new StatsHeatmapDto.StreakDto(
                        stats.streak().current(),
                        stats.streak().longest()
                )
        );
    }

    public StatsHistogramsDto toHistogramsDto(HistogramStats stats, String deckIdParam, String window) {
        return new StatsHistogramsDto(
                deckIdParam != null ? deckIdParam.toString() : "all",
                window,
                new StatsHistogramsDto.HistogramDto(
                        stats.intervals().bins().stream()
                                .map(b -> new StatsHistogramsDto.BinDto(b.label(), b.min(), b.max(), b.count()))
                                .collect(Collectors.toList()),
                        stats.intervals().total()
                ),
                new StatsHistogramsDto.HistogramDto(
                        stats.ease().bins().stream()
                                .map(b -> new StatsHistogramsDto.BinDto(b.label(), b.min(), b.max(), b.count()))
                                .collect(Collectors.toList()),
                        stats.ease().total()
                ),
                new StatsHistogramsDto.AnswerButtonsDto(
                        stats.answerButtons().again(),
                        stats.answerButtons().hard(),
                        stats.answerButtons().good(),
                        stats.answerButtons().easy()
                ),
                stats.dueForecast().stream()
                        .map(d -> new StatsHistogramsDto.DuePointDto(d.date(), d.due()))
                        .collect(Collectors.toList())
        );
    }
}
