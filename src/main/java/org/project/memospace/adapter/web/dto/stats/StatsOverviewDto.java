package org.project.memospace.adapter.web.dto.stats;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Schema(description = "Statistics overview response")
public record StatsOverviewDto(
        @Schema(description = "Deck ID or 'all'")
        String deckId,

        @Schema(description = "Window period", example = "30d")
        String window,

        @Schema(description = "Date range information")
        DateRangeDto range,

        @Schema(description = "Time series data")
        SeriesDto series,

        @Schema(description = "Aggregate totals")
        TotalsDto totals
) {
    public record DateRangeDto(
            LocalDate from,
            LocalDate to,
            String timezone,
            int cutoffHour
    ) {}

    public record SeriesDto(
            List<ReviewsPerDayDto> reviewsPerDay,
            List<RetentionPerDayDto> retentionPerDay,
            List<AnswerButtonsPerDayDto> answerButtonsPerDay,
            List<TimeSpentPerDayDto> timeSpentPerDayMs
    ) {}

    public record ReviewsPerDayDto(LocalDate date, int count) {}

    public record RetentionPerDayDto(LocalDate date, int passed, int total, double ratio) {}

    public record AnswerButtonsPerDayDto(LocalDate date, int again, int hard, int good, int easy) {}

    public record TimeSpentPerDayDto(LocalDate date, @JsonProperty("ms") long ms) {}

    public record TotalsDto(
            int reviews,
            int passed,
            double retentionRatio,
            long timeSpentMs
    ) {}
}
