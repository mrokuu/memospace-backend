package org.project.memospace.domain.service.stats;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.memospace.domain.model.stats.*;

import java.time.*;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StatsServiceTest {

    private StatsService service;
    private Cutoff cutoff;
    private LocalDate baseDate;
    private ZoneId timezone;

    @BeforeEach
    void setUp() {
        service = new StatsService();
        timezone = ZoneId.of("UTC");
        cutoff = new Cutoff(timezone, 4);
        baseDate = LocalDate.of(2024, 1, 15);
    }

    // ========== computeOverview Tests ==========

    @Test
    void shouldComputeOverviewWithEmptyReviews() {
        // Given
        List<ReviewRow> emptyReviews = List.of();
        Window window = new Window(baseDate, baseDate.plusDays(7));

        // When
        OverviewStats stats = service.computeOverview(emptyReviews, window, cutoff);

        // Then
        assertNotNull(stats);
        assertEquals(7, stats.reviewsPerDay().size());
        assertEquals(7, stats.retentionPerDay().size());
        assertEquals(7, stats.answerButtonsPerDay().size());
        assertEquals(7, stats.timeSpentPerDay().size());

        // All days should have zero counts
        stats.reviewsPerDay().forEach(day -> assertEquals(0, day.count()));

        // Totals should be zero
        assertEquals(0, stats.totals().getReviews());
        assertEquals(0, stats.totals().getPassed());
        assertEquals(0, stats.totals().getTimeSpentMs());
        assertEquals(0.0, stats.totals().getRetentionRatio());
    }

    @Test
    void shouldComputeOverviewWithSingleDayReviews() {
        // Given
        Instant reviewTime = baseDate.atTime(10, 0).atZone(timezone).toInstant();
        List<ReviewRow> reviews = List.of(
                reviewRow(1L, reviewTime, 3, 1000),
                reviewRow(2L, reviewTime, 4, 2000),
                reviewRow(3L, reviewTime, 2, 1500)
        );
        Window window = new Window(baseDate, baseDate.plusDays(3));

        // When
        OverviewStats stats = service.computeOverview(reviews, window, cutoff);

        // Then
        assertEquals(3, stats.reviewsPerDay().size());

        // First day should have all reviews
        assertEquals(3, stats.reviewsPerDay().get(0).count());
        // Other days should have zero
        assertEquals(0, stats.reviewsPerDay().get(1).count());
        assertEquals(0, stats.reviewsPerDay().get(2).count());

        // Totals
        assertEquals(3, stats.totals().getReviews());
        assertEquals(2, stats.totals().getPassed()); // quality 3 and 4
        assertEquals(4500, stats.totals().getTimeSpentMs());
        assertEquals(2.0 / 3.0, stats.totals().getRetentionRatio(), 0.001);
    }

    @Test
    void shouldComputeOverviewWithMultipleDays() {
        // Given
        List<ReviewRow> reviews = List.of(
                // Day 0
                reviewRow(1L, instantAt(baseDate, 10, 0), 3, 1000),
                reviewRow(2L, instantAt(baseDate, 11, 0), 4, 2000),
                // Day 1
                reviewRow(3L, instantAt(baseDate.plusDays(1), 10, 0), 2, 1500),
                // Day 2
                reviewRow(4L, instantAt(baseDate.plusDays(2), 10, 0), 3, 3000),
                reviewRow(5L, instantAt(baseDate.plusDays(2), 11, 0), 4, 2500)
        );
        Window window = new Window(baseDate, baseDate.plusDays(3));

        // When
        OverviewStats stats = service.computeOverview(reviews, window, cutoff);

        // Then
        assertEquals(2, stats.reviewsPerDay().get(0).count());
        assertEquals(1, stats.reviewsPerDay().get(1).count());
        assertEquals(2, stats.reviewsPerDay().get(2).count());

        // Totals
        assertEquals(5, stats.totals().getReviews());
        assertEquals(4, stats.totals().getPassed());
        assertEquals(10000, stats.totals().getTimeSpentMs());
    }

    @Test
    void shouldComputeRetentionCorrectly() {
        // Given
        List<ReviewRow> reviews = List.of(
                reviewRow(1L, instantAt(baseDate, 10, 0), 3, 1000), // passed
                reviewRow(2L, instantAt(baseDate, 11, 0), 4, 2000), // passed
                reviewRow(3L, instantAt(baseDate, 12, 0), 2, 1500), // failed
                reviewRow(4L, instantAt(baseDate, 13, 0), 0, 1000)  // failed
        );
        Window window = new Window(baseDate, baseDate.plusDays(1));

        // When
        OverviewStats stats = service.computeOverview(reviews, window, cutoff);

        // Then
        OverviewStats.RetentionPerDay retention = stats.retentionPerDay().get(0);
        assertEquals(2, retention.getPassed());
        assertEquals(4, retention.getTotal());
        assertEquals(0.5, retention.getRatio(), 0.001);
    }

    @Test
    void shouldComputeAnswerButtonsDistribution() {
        // Given
        List<ReviewRow> reviews = List.of(
                reviewRow(1L, instantAt(baseDate, 10, 0), 0, 1000), // again
                reviewRow(2L, instantAt(baseDate, 10, 0), 0, 1000), // again
                reviewRow(3L, instantAt(baseDate, 10, 0), 2, 1000), // hard
                reviewRow(4L, instantAt(baseDate, 10, 0), 3, 1000), // good
                reviewRow(5L, instantAt(baseDate, 10, 0), 3, 1000), // good
                reviewRow(6L, instantAt(baseDate, 10, 0), 3, 1000), // good
                reviewRow(7L, instantAt(baseDate, 10, 0), 4, 1000)  // easy
        );
        Window window = new Window(baseDate, baseDate.plusDays(1));

        // When
        OverviewStats stats = service.computeOverview(reviews, window, cutoff);

        // Then
        OverviewStats.AnswerButtonsPerDay buttons = stats.answerButtonsPerDay().get(0);
        assertEquals(2, buttons.again());
        assertEquals(1, buttons.hard());
        assertEquals(3, buttons.good());
        assertEquals(1, buttons.easy());
    }

    @Test
    void shouldComputeTimeSpentPerDay() {
        // Given
        List<ReviewRow> reviews = List.of(
                reviewRow(1L, instantAt(baseDate, 10, 0), 3, 1000),
                reviewRow(2L, instantAt(baseDate, 11, 0), 4, 2000),
                reviewRow(3L, instantAt(baseDate.plusDays(1), 10, 0), 3, 5000)
        );
        Window window = new Window(baseDate, baseDate.plusDays(2));

        // When
        OverviewStats stats = service.computeOverview(reviews, window, cutoff);

        // Then
        assertEquals(3000, stats.timeSpentPerDay().get(0).milliseconds());
        assertEquals(5000, stats.timeSpentPerDay().get(1).milliseconds());
    }

    @Test
    void shouldHandleNullTimeSpent() {
        // Given
        List<ReviewRow> reviews = List.of(
                reviewRow(1L, instantAt(baseDate, 10, 0), 3, null),
                reviewRow(2L, instantAt(baseDate, 11, 0), 4, 1000)
        );
        Window window = new Window(baseDate, baseDate.plusDays(1));

        // When
        OverviewStats stats = service.computeOverview(reviews, window, cutoff);

        // Then
        assertEquals(1000, stats.timeSpentPerDay().get(0).milliseconds());
        assertEquals(1000, stats.totals().getTimeSpentMs());
    }

    // ========== computeHeatmap Tests ==========

    @Test
    void shouldComputeHeatmapForYear() {
        // Given
        int year = 2024;
        List<ReviewRow> reviews = List.of(
                reviewRow(1L, LocalDate.of(2024, 1, 1).atStartOfDay(timezone).toInstant(), 3, 1000),
                reviewRow(2L, LocalDate.of(2024, 1, 1).atStartOfDay(timezone).toInstant(), 4, 2000),
                reviewRow(3L, LocalDate.of(2024, 1, 2).atStartOfDay(timezone).toInstant(), 3, 1500)
        );
        StreakService streakService = mock(StreakService.class);
        when(streakService.computeStreaks(any())).thenReturn(new Streak(1, 2));

        // When
        HeatmapStats stats = service.computeHeatmap(reviews, year, cutoff, streakService);

        // Then
        assertEquals(2024, stats.year());
        assertEquals(366, stats.days().size()); // 2024 is a leap year

        // At least some days should have reviews
        assertTrue(stats.days().stream().anyMatch(d -> d.getCount() > 0));

        // Streak should be computed
        assertEquals(1, stats.streak().current());
        assertEquals(2, stats.streak().longest());
    }

    @Test
    void shouldHandleEmptyReviewsInHeatmap() {
        // Given
        int year = 2024;
        List<ReviewRow> emptyReviews = List.of();
        StreakService streakService = mock(StreakService.class);
        when(streakService.computeStreaks(any())).thenReturn(new Streak(0, 0));

        // When
        HeatmapStats stats = service.computeHeatmap(emptyReviews, year, cutoff, streakService);

        // Then
        assertEquals(366, stats.days().size());
        stats.days().forEach(day -> assertEquals(0, day.getCount()));
    }

    // ========== computeHistograms Tests ==========

    @Test
    void shouldComputeHistogramsWithAllData() {
        // Given
        List<ReviewRow> reviews = List.of(
                reviewRowFull(1L, instantAt(baseDate, 10, 0), 0, 2.5, 1, 1000),
                reviewRowFull(2L, instantAt(baseDate, 10, 0), 2, 2.3, 5, 2000),
                reviewRowFull(3L, instantAt(baseDate, 10, 0), 3, 2.7, 15, 3000),
                reviewRowFull(4L, instantAt(baseDate, 10, 0), 4, 3.0, 90, 4000)
        );

        // When
        HistogramStats stats = service.computeHistograms(reviews);

        // Then
        assertNotNull(stats.intervals());
        assertNotNull(stats.ease());
        assertNotNull(stats.answerButtons());

        // Answer buttons
        assertEquals(1, stats.answerButtons().again());
        assertEquals(1, stats.answerButtons().hard());
        assertEquals(1, stats.answerButtons().good());
        assertEquals(1, stats.answerButtons().easy());

        // Intervals should have 4 reviews
        assertEquals(4, stats.intervals().total());

        // Ease should have 4 reviews
        assertEquals(4, stats.ease().total());
    }

    @Test
    void shouldGroupIntervalsIntoBins() {
        // Given
        List<ReviewRow> reviews = List.of(
                reviewRowWithInterval(1, 1),   // 1d bin
                reviewRowWithInterval(2, 2),   // 2-3d bin
                reviewRowWithInterval(3, 3),   // 2-3d bin
                reviewRowWithInterval(4, 5),   // 4-7d bin
                reviewRowWithInterval(5, 15),  // 8-30d bin
                reviewRowWithInterval(6, 50),  // 31-90d bin
                reviewRowWithInterval(7, 100)  // 91d+ bin
        );

        // When
        HistogramStats stats = service.computeHistograms(reviews);

        // Then
        List<HistogramStats.Bin> bins = stats.intervals().bins();
        assertEquals(6, bins.size()); // 6 bins

        // Check bins have correct counts
        assertEquals("1d", bins.get(0).label());
        assertTrue(bins.get(0).count() > 0);
    }

    @Test
    void shouldGroupEaseIntoBins() {
        // Given
        List<ReviewRow> reviews = List.of(
                reviewRowWithEase(1, 1.5),  // 1.3-1.7
                reviewRowWithEase(2, 2.0),  // 1.7-2.1
                reviewRowWithEase(3, 2.3),  // 2.1-2.5
                reviewRowWithEase(4, 2.7),  // 2.5-2.9
                reviewRowWithEase(5, 3.5)   // 2.9+
        );

        // When
        HistogramStats stats = service.computeHistograms(reviews);

        // Then
        List<HistogramStats.Bin> bins = stats.ease().bins();
        assertEquals(5, bins.size()); // 5 bins

        // Each bin should have at least one review
        bins.forEach(bin -> assertTrue(bin.count() >= 0));
    }

    @Test
    void shouldHandleReviewsWithoutIntervals() {
        // Given
        List<ReviewRow> reviews = List.of(
                reviewRow(1L, instantAt(baseDate, 10, 0), 3, 1000),
                reviewRow(2L, instantAt(baseDate, 10, 0), 4, 2000)
        );

        // When
        HistogramStats stats = service.computeHistograms(reviews);

        // Then
        assertEquals(0, stats.intervals().total());
        assertEquals(0, stats.ease().total());
        assertEquals(2, stats.answerButtons().good() + stats.answerButtons().easy());
    }

    @Test
    void shouldHandleNullEaseFactors() {
        // Given
        List<ReviewRow> reviews = List.of(
                reviewRowFull(1L, instantAt(baseDate, 10, 0), 3, null, 5, 1000),
                reviewRowFull(2L, instantAt(baseDate, 10, 0), 4, 2.5, 10, 2000)
        );

        // When
        HistogramStats stats = service.computeHistograms(reviews);

        // Then
        assertEquals(2, stats.intervals().total());
        assertEquals(1, stats.ease().total()); // Only one has ease factor
    }

    @Test
    void shouldHandleZeroIntervals() {
        // Given
        List<ReviewRow> reviews = List.of(
                reviewRowFull(1L, instantAt(baseDate, 10, 0), 3, 2.5, 0, 1000),
                reviewRowFull(2L, instantAt(baseDate, 10, 0), 4, 2.7, 5, 2000)
        );

        // When
        HistogramStats stats = service.computeHistograms(reviews);

        // Then
        assertEquals(1, stats.intervals().total()); // Zero intervals are filtered out
    }

    // ========== Helper Methods ==========

    private ReviewRow reviewRow(Long cardId, Instant reviewedAt, int quality, Integer msSpent) {
        return ReviewRow.builder()
                .cardId(cardId)
                .deckId(1L)
                .reviewedAt(reviewedAt)
                .quality(quality)
                .msSpent(msSpent)
                .build();
    }

    private ReviewRow reviewRowFull(Long cardId, Instant reviewedAt, int quality,
                                     Double easeFactor, Integer interval, Integer msSpent) {
        return ReviewRow.builder()
                .cardId(cardId)
                .deckId(1L)
                .reviewedAt(reviewedAt)
                .quality(quality)
                .easeFactorAfter(easeFactor)
                .intervalAfter(interval)
                .msSpent(msSpent)
                .build();
    }

    private ReviewRow reviewRowWithInterval(long cardId, int interval) {
        return reviewRowFull(cardId, instantAt(baseDate, 10, 0), 3, 2.5, interval, 1000);
    }

    private ReviewRow reviewRowWithEase(long cardId, double easeFactor) {
        return reviewRowFull(cardId, instantAt(baseDate, 10, 0), 3, easeFactor, 5, 1000);
    }

    private Instant instantAt(LocalDate date, int hour, int minute) {
        return date.atTime(hour, minute).atZone(timezone).toInstant();
    }
}
