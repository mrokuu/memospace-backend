package org.project.memospace.domain.service.stats;

import org.project.memospace.domain.model.stats.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Domain service for computing statistics from review data.
 * Pure business logic with no framework dependencies.
 */
public class StatsService {

    /**
     * Compute overview statistics from review rows.
     */
    public OverviewStats computeOverview(List<ReviewRow> reviews, Window window, Cutoff cutoff) {
        // Group reviews by local date
        Map<LocalDate, List<ReviewRow>> reviewsByDate = reviews.stream()
                .collect(Collectors.groupingBy(r -> cutoff.toLocalDate(r.reviewedAt())));

        // Generate all dates in the window
        List<LocalDate> allDates = generateDateRange(window.fromInclusive(), window.toExclusive());

        // Compute daily series
        List<OverviewStats.ReviewsPerDay> reviewsPerDay = allDates.stream()
                .map(date -> {
                    List<ReviewRow> dayReviews = reviewsByDate.getOrDefault(date, List.of());
                    return new OverviewStats.ReviewsPerDay(date, dayReviews.size());
                })
                .collect(Collectors.toList());

        List<OverviewStats.RetentionPerDay> retentionPerDay = allDates.stream()
                .map(date -> {
                    List<ReviewRow> dayReviews = reviewsByDate.getOrDefault(date, List.of());
                    int total = (int) dayReviews.stream().filter(ReviewRow::isAnswered).count();
                    int passed = (int) dayReviews.stream().filter(ReviewRow::isPassed).count();
                    return new OverviewStats.RetentionPerDay(date, passed, total);
                })
                .collect(Collectors.toList());

        List<OverviewStats.AnswerButtonsPerDay> answerButtonsPerDay = allDates.stream()
                .map(date -> {
                    List<ReviewRow> dayReviews = reviewsByDate.getOrDefault(date, List.of());
                    int again = (int) dayReviews.stream().filter(r -> r.quality() == 0).count();
                    int hard = (int) dayReviews.stream().filter(r -> r.quality() == 2).count();
                    int good = (int) dayReviews.stream().filter(r -> r.quality() == 3).count();
                    int easy = (int) dayReviews.stream().filter(r -> r.quality() == 4).count();
                    return new OverviewStats.AnswerButtonsPerDay(date, again, hard, good, easy);
                })
                .collect(Collectors.toList());

        List<OverviewStats.TimeSpentPerDay> timeSpentPerDay = allDates.stream()
                .map(date -> {
                    List<ReviewRow> dayReviews = reviewsByDate.getOrDefault(date, List.of());
                    long totalMs = dayReviews.stream()
                            .mapToLong(r -> r.msSpent() != null ? r.msSpent() : 0)
                            .sum();
                    return new OverviewStats.TimeSpentPerDay(date, totalMs);
                })
                .collect(Collectors.toList());

        // Compute totals
        int totalReviews = (int) reviews.stream().filter(ReviewRow::isAnswered).count();
        int totalPassed = (int) reviews.stream().filter(ReviewRow::isPassed).count();
        long totalTimeMs = reviews.stream()
                .mapToLong(r -> r.msSpent() != null ? r.msSpent() : 0)
                .sum();

        OverviewStats.Totals totals = new OverviewStats.Totals(totalReviews, totalPassed, totalTimeMs);

        return new OverviewStats(reviewsPerDay, retentionPerDay, answerButtonsPerDay, timeSpentPerDay, totals);
    }

    /**
     * Compute heatmap statistics for a year.
     */
    public HeatmapStats computeHeatmap(List<ReviewRow> reviews, int year, Cutoff cutoff, StreakService streakService) {
        // Group reviews by local date
        Map<LocalDate, Long> reviewCountByDate = reviews.stream()
                .map(r -> cutoff.toLocalDate(r.reviewedAt()))
                .collect(Collectors.groupingBy(date -> date, Collectors.counting()));

        // Generate all dates in the year
        LocalDate yearStart = LocalDate.of(year, 1, 1);
        LocalDate yearEnd = LocalDate.of(year, 12, 31).plusDays(1);
        List<LocalDate> allDates = generateDateRange(yearStart, yearEnd);

        List<HeatmapStats.DayCount> days = allDates.stream()
                .map(date -> new HeatmapStats.DayCount(date, reviewCountByDate.getOrDefault(date, 0L).intValue()))
                .collect(Collectors.toList());

        // Compute streaks
        Map<LocalDate, Integer> countMap = reviewCountByDate.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().intValue()));
        Streak streak = streakService.computeStreaks(countMap);

        return new HeatmapStats(year, days, streak);
    }

    /**
     * Compute histogram statistics.
     */
    public HistogramStats computeHistograms(List<ReviewRow> reviews) {
        // Interval histogram (only for reviews with interval data)
        List<ReviewRow> withIntervals = reviews.stream()
                .filter(r -> r.intervalAfter() != null && r.intervalAfter() > 0)
                .collect(Collectors.toList());

        HistogramStats.IntervalHistogram intervals = computeIntervalHistogram(withIntervals);

        // Ease histogram (only for reviews with ease data)
        List<ReviewRow> withEase = reviews.stream()
                .filter(r -> r.easeFactorAfter() != null)
                .collect(Collectors.toList());

        HistogramStats.EaseHistogram ease = computeEaseHistogram(withEase);

        // Answer buttons totals
        int again = (int) reviews.stream().filter(r -> r.quality() == 0).count();
        int hard = (int) reviews.stream().filter(r -> r.quality() == 2).count();
        int good = (int) reviews.stream().filter(r -> r.quality() == 3).count();
        int easy = (int) reviews.stream().filter(r -> r.quality() == 4).count();

        HistogramStats.AnswerButtons answerButtons = new HistogramStats.AnswerButtons(again, hard, good, easy);

        return new HistogramStats(intervals, ease, answerButtons, List.of());
    }

    private HistogramStats.IntervalHistogram computeIntervalHistogram(List<ReviewRow> reviews) {
        List<IntervalBinConfig> binConfigs = IntervalBinConfig.DEFAULT_BINS;

        List<HistogramStats.Bin> bins = binConfigs.stream()
                .map(config -> {
                    int count = (int) reviews.stream()
                            .filter(r -> config.contains(r.intervalAfter()))
                            .count();
                    return new HistogramStats.Bin(config.label, config.min, config.max, count);
                })
                .collect(Collectors.toList());

        return new HistogramStats.IntervalHistogram(bins, reviews.size());
    }

    private HistogramStats.EaseHistogram computeEaseHistogram(List<ReviewRow> reviews) {
        List<EaseBinConfig> binConfigs = EaseBinConfig.DEFAULT_BINS;

        List<HistogramStats.Bin> bins = binConfigs.stream()
                .map(config -> {
                    int count = (int) reviews.stream()
                            .filter(r -> config.contains(r.easeFactorAfter()))
                            .count();
                    return new HistogramStats.Bin(config.label, config.min, config.max, count);
                })
                .collect(Collectors.toList());

        return new HistogramStats.EaseHistogram(bins, reviews.size());
    }

    private List<LocalDate> generateDateRange(LocalDate start, LocalDate endExclusive) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate current = start;
        while (current.isBefore(endExclusive)) {
            dates.add(current);
            current = current.plusDays(1);
        }
        return dates;
    }

    // Bin configurations
    private static class IntervalBinConfig {
        static final List<IntervalBinConfig> DEFAULT_BINS = List.of(
                new IntervalBinConfig("1d", 1.0, 1.0),
                new IntervalBinConfig("2–3d", 2.0, 3.0),
                new IntervalBinConfig("4–7d", 4.0, 7.0),
                new IntervalBinConfig("8–30d", 8.0, 30.0),
                new IntervalBinConfig("31–90d", 31.0, 90.0),
                new IntervalBinConfig("91d+", 91.0, null)
        );

        final String label;
        final Double min;
        final Double max;

        IntervalBinConfig(String label, Double min, Double max) {
            this.label = label;
            this.min = min;
            this.max = max;
        }

        boolean contains(Integer value) {
            if (value == null) return false;
            if (max == null) return value >= min;
            return value >= min && value <= max;
        }
    }

    private static class EaseBinConfig {
        static final List<EaseBinConfig> DEFAULT_BINS = List.of(
                new EaseBinConfig("1.3–1.7", 1.3, 1.7),
                new EaseBinConfig("1.7–2.1", 1.7, 2.1),
                new EaseBinConfig("2.1–2.5", 2.1, 2.5),
                new EaseBinConfig("2.5–2.9", 2.5, 2.9),
                new EaseBinConfig("2.9+", 2.9, null)
        );

        final String label;
        final Double min;
        final Double max;

        EaseBinConfig(String label, Double min, Double max) {
            this.label = label;
            this.min = min;
            this.max = max;
        }

        boolean contains(Double value) {
            if (value == null) return false;
            if (max == null) return value >= min;
            return value >= min && value <= max;
        }
    }
}
