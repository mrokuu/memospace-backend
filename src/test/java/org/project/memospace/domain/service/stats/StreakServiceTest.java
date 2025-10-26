package org.project.memospace.domain.service.stats;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.memospace.domain.model.stats.Streak;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StreakServiceTest {

    private StreakService service;
    private LocalDate baseDate;

    @BeforeEach
    void setUp() {
        service = new StreakService();
        baseDate = LocalDate.of(2024, 1, 15);
    }

    @Test
    void shouldReturnZeroStreaksForEmptyData() {
        // Given
        Map<LocalDate, Integer> emptyMap = new HashMap<>();

        // When
        Streak result = service.computeStreaks(emptyMap);

        // Then
        assertEquals(0, result.current());
        assertEquals(0, result.longest());
    }

    @Test
    void shouldReturnZeroStreaksForAllZeroCounts() {
        // Given
        Map<LocalDate, Integer> dataWithZeros = Map.of(
                baseDate, 0,
                baseDate.plusDays(1), 0,
                baseDate.plusDays(2), 0
        );

        // When
        Streak result = service.computeStreaks(dataWithZeros);

        // Then
        assertEquals(0, result.current());
        assertEquals(0, result.longest());
    }

    @Test
    void shouldComputeSingleDayStreak() {
        // Given
        Map<LocalDate, Integer> singleDay = Map.of(baseDate, 5);

        // When
        Streak result = service.computeStreaks(singleDay);

        // Then
        assertEquals(1, result.current());
        assertEquals(1, result.longest());
    }

    @Test
    void shouldComputeConsecutiveDaysStreak() {
        // Given
        Map<LocalDate, Integer> consecutiveDays = Map.of(
                baseDate, 1,
                baseDate.plusDays(1), 2,
                baseDate.plusDays(2), 3,
                baseDate.plusDays(3), 1
        );

        // When
        Streak result = service.computeStreaks(consecutiveDays);

        // Then
        assertEquals(4, result.current());
        assertEquals(4, result.longest());
    }

    @Test
    void shouldIdentifyLongestStreakWithGaps() {
        // Given
        Map<LocalDate, Integer> dataWithGaps = Map.of(
                baseDate, 1,
                baseDate.plusDays(1), 1,
                baseDate.plusDays(2), 1,
                // Gap of 2 days
                baseDate.plusDays(5), 1,
                baseDate.plusDays(6), 1,
                baseDate.plusDays(7), 1,
                baseDate.plusDays(8), 1
        );

        // When
        Streak result = service.computeStreaks(dataWithGaps);

        // Then
        assertEquals(4, result.current()); // Last streak is 4 days
        assertEquals(4, result.longest()); // Longest is also 4 days
    }

    @Test
    void shouldSetLongestToEarliestStreak() {
        // Given - First streak is longer
        Map<LocalDate, Integer> data = Map.of(
                baseDate, 1,
                baseDate.plusDays(1), 1,
                baseDate.plusDays(2), 1,
                baseDate.plusDays(3), 1,
                baseDate.plusDays(4), 1,
                // Gap
                baseDate.plusDays(7), 1,
                baseDate.plusDays(8), 1
        );

        // When
        Streak result = service.computeStreaks(data);

        // Then
        assertEquals(2, result.current()); // Last streak is 2 days
        assertEquals(5, result.longest()); // Longest is 5 days
    }

    @Test
    void shouldHandleNonConsecutiveDates() {
        // Given - Reviews only on odd dates
        Map<LocalDate, Integer> oddDatesOnly = Map.of(
                baseDate, 1,
                baseDate.plusDays(2), 1,
                baseDate.plusDays(4), 1
        );

        // When
        Streak result = service.computeStreaks(oddDatesOnly);

        // Then
        assertEquals(1, result.current()); // Each day is a separate streak
        assertEquals(1, result.longest());
    }

    @Test
    void shouldComputeStreaksWithTodayInCurrentStreak() {
        // Given
        LocalDate today = baseDate.plusDays(5);
        Map<LocalDate, Integer> recentStreak = Map.of(
                baseDate.plusDays(3), 1,
                baseDate.plusDays(4), 1,
                baseDate.plusDays(5), 1 // today
        );

        // When
        Streak result = service.computeStreaksWithToday(recentStreak, today);

        // Then
        assertEquals(3, result.current());
        assertEquals(3, result.longest());
    }

    @Test
    void shouldComputeStreaksWithTodayMinusOneDayLenient() {
        // Given - Last review was yesterday (should still count)
        LocalDate today = baseDate.plusDays(5);
        Map<LocalDate, Integer> yesterdayStreak = Map.of(
                baseDate.plusDays(2), 1,
                baseDate.plusDays(3), 1,
                baseDate.plusDays(4), 1 // yesterday
        );

        // When
        Streak result = service.computeStreaksWithToday(yesterdayStreak, today);

        // Then
        assertEquals(3, result.current()); // Should still count
        assertEquals(3, result.longest());
    }

    @Test
    void shouldNotCountCurrentStreakIfTooOld() {
        // Given - Last review was 3 days ago
        LocalDate today = baseDate.plusDays(5);
        Map<LocalDate, Integer> oldStreak = Map.of(
                baseDate, 1,
                baseDate.plusDays(1), 1,
                baseDate.plusDays(2), 1 // 3 days ago
        );

        // When
        Streak result = service.computeStreaksWithToday(oldStreak, today);

        // Then
        assertEquals(0, result.current()); // Should not count as current
        assertEquals(3, result.longest()); // But longest is still 3
    }

    @Test
    void shouldHandleMultipleStreaksWithTodayCheck() {
        // Given - Multiple streaks, most recent doesn't include today
        LocalDate today = baseDate.plusDays(10);
        Map<LocalDate, Integer> multipleStreaks = Map.of(
                baseDate, 1,
                baseDate.plusDays(1), 1,
                baseDate.plusDays(2), 1,
                baseDate.plusDays(3), 1,
                baseDate.plusDays(4), 1, // 5-day streak
                // Gap
                baseDate.plusDays(7), 1,
                baseDate.plusDays(8), 1 // 2-day streak ending 2 days ago
        );

        // When
        Streak result = service.computeStreaksWithToday(multipleStreaks, today);

        // Then
        assertEquals(0, result.current()); // No current streak
        assertEquals(5, result.longest()); // Longest is 5
    }

    @Test
    void shouldIgnoreDatesWithZeroReviews() {
        // Given
        Map<LocalDate, Integer> mixedData = Map.of(
                baseDate, 5,
                baseDate.plusDays(1), 0, // Should be ignored
                baseDate.plusDays(2), 3
        );

        // When
        Streak result = service.computeStreaks(mixedData);

        // Then
        assertEquals(1, result.current()); // Each non-zero day is separate
        assertEquals(1, result.longest());
    }

    @Test
    void shouldHandleUnsortedDates() {
        // Given - Dates provided out of order
        Map<LocalDate, Integer> unsortedData = new HashMap<>();
        unsortedData.put(baseDate.plusDays(3), 1);
        unsortedData.put(baseDate, 1);
        unsortedData.put(baseDate.plusDays(2), 1);
        unsortedData.put(baseDate.plusDays(1), 1);

        // When
        Streak result = service.computeStreaks(unsortedData);

        // Then
        assertEquals(4, result.current());
        assertEquals(4, result.longest());
    }

    @Test
    void shouldHandleVeryLongStreak() {
        // Given - 100 consecutive days
        Map<LocalDate, Integer> longStreak = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            longStreak.put(baseDate.plusDays(i), 1);
        }

        // When
        Streak result = service.computeStreaks(longStreak);

        // Then
        assertEquals(100, result.current());
        assertEquals(100, result.longest());
    }
}
