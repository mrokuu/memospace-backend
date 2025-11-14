package org.memospace.web.dto.stats;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Heatmap statistics response")
public record StatsHeatmapDto(
        @Schema(description = "Year", example = "2025")
        int year,

        @Schema(description = "Timezone", example = "Europe/Warsaw")
        String timezone,

        @Schema(description = "Cutoff hour", example = "4")
        int cutoffHour,

        @Schema(description = "Reviews per day")
        List<DayCountDto> days,

        @Schema(description = "Streak information")
        StreakDto streak
) {
    public record DayCountDto(LocalDate date, int count) {}

    public record StreakDto(int current, int longest) {}
}
