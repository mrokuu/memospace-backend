package org.memospace.model.stats;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Heatmap statistics showing reviews per day for a year.
 */
@Builder(toBuilder = true)
public record HeatmapStats(int year, List<DayCount> days, Streak streak) {
    public HeatmapStats(int year, List<DayCount> days, Streak streak) {
        this.year = year;
        this.days = Objects.requireNonNull(days);
        this.streak = Objects.requireNonNull(streak);
    }

    @Value
    @Builder(toBuilder = true)
    public static class DayCount {
        LocalDate date;
        int count;

        public DayCount(LocalDate date, int count) {
            this.date = Objects.requireNonNull(date);
            this.count = count;
        }
    }
}
