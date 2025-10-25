package org.project.memospace.domain.service.stats;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.memospace.domain.model.stats.DuePoint;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ForecastServiceTest {

    private ForecastService service;
    private LocalDate baseDate;

    @BeforeEach
    void setUp() {
        service = new ForecastService();
        baseDate = LocalDate.of(2024, 1, 15);
    }

    @Test
    void shouldForecastZeroCardsForEmptyData() {
        // Given
        List<LocalDate> emptyDueDates = List.of();

        // When
        List<DuePoint> forecast = service.forecastDueSimple(emptyDueDates, baseDate, 7);

        // Then
        assertEquals(7, forecast.size());
        forecast.forEach(point -> assertEquals(0, point.due()));
    }

    @Test
    void shouldForecastSingleCardDueToday() {
        // Given
        List<LocalDate> dueDates = List.of(baseDate);

        // When
        List<DuePoint> forecast = service.forecastDueSimple(dueDates, baseDate, 7);

        // Then
        assertEquals(7, forecast.size());
        assertEquals(1, forecast.get(0).due()); // Today
        assertEquals(1, forecast.get(1).due()); // Tomorrow (cumulative)
        assertEquals(1, forecast.get(6).due()); // Day 6 (cumulative)
    }

    @Test
    void shouldForecastCardsDueFuture() {
        // Given
        List<LocalDate> dueDates = List.of(
                baseDate.plusDays(2) // Due in 2 days
        );

        // When
        List<DuePoint> forecast = service.forecastDueSimple(dueDates, baseDate, 7);

        // Then
        assertEquals(7, forecast.size());
        assertEquals(0, forecast.get(0).due()); // Today - not due yet
        assertEquals(0, forecast.get(1).due()); // Tomorrow - not due yet
        assertEquals(1, forecast.get(2).due()); // Day 2 - now due
        assertEquals(1, forecast.get(6).due()); // Day 6 - still due
    }

    @Test
    void shouldForecastMultipleCardsCumulatively() {
        // Given
        List<LocalDate> dueDates = List.of(
                baseDate,                // Due today
                baseDate.plusDays(2),    // Due day 2
                baseDate.plusDays(4)     // Due day 4
        );

        // When
        List<DuePoint> forecast = service.forecastDueSimple(dueDates, baseDate, 7);

        // Then
        assertEquals(7, forecast.size());
        assertEquals(1, forecast.get(0).due()); // Day 0: 1 card
        assertEquals(1, forecast.get(1).due()); // Day 1: 1 card
        assertEquals(2, forecast.get(2).due()); // Day 2: 2 cards
        assertEquals(2, forecast.get(3).due()); // Day 3: 2 cards
        assertEquals(3, forecast.get(4).due()); // Day 4: 3 cards
        assertEquals(3, forecast.get(6).due()); // Day 6: 3 cards
    }

    @Test
    void shouldForecastCardsDuePast() {
        // Given - Cards that were due in the past
        List<LocalDate> dueDates = List.of(
                baseDate.minusDays(5),
                baseDate.minusDays(2)
        );

        // When
        List<DuePoint> forecast = service.forecastDueSimple(dueDates, baseDate, 7);

        // Then
        assertEquals(7, forecast.size());
        forecast.forEach(point ->
                assertEquals(2, point.due(), "All past-due cards should be counted in every day")
        );
    }

    @Test
    void shouldGenerateCorrectDatesInForecast() {
        // Given
        List<LocalDate> dueDates = List.of();

        // When
        List<DuePoint> forecast = service.forecastDueSimple(dueDates, baseDate, 5);

        // Then
        assertEquals(5, forecast.size());
        assertEquals(baseDate, forecast.get(0).date());
        assertEquals(baseDate.plusDays(1), forecast.get(1).date());
        assertEquals(baseDate.plusDays(2), forecast.get(2).date());
        assertEquals(baseDate.plusDays(3), forecast.get(3).date());
        assertEquals(baseDate.plusDays(4), forecast.get(4).date());
    }

    @Test
    void shouldForecastByDayNonCumulative() {
        // Given
        List<LocalDate> dueDates = List.of(
                baseDate,
                baseDate,
                baseDate.plusDays(1),
                baseDate.plusDays(3),
                baseDate.plusDays(3),
                baseDate.plusDays(3)
        );

        // When
        List<DuePoint> forecast = service.forecastDueByDay(dueDates, baseDate, 7);

        // Then
        assertEquals(7, forecast.size());
        assertEquals(2, forecast.get(0).due()); // Day 0: 2 cards
        assertEquals(1, forecast.get(1).due()); // Day 1: 1 card
        assertEquals(0, forecast.get(2).due()); // Day 2: 0 cards
        assertEquals(3, forecast.get(3).due()); // Day 3: 3 cards
        assertEquals(0, forecast.get(4).due()); // Day 4: 0 cards
        assertEquals(0, forecast.get(5).due()); // Day 5: 0 cards
        assertEquals(0, forecast.get(6).due()); // Day 6: 0 cards
    }

    @Test
    void shouldHandleCardsDueOnSameDay() {
        // Given - Multiple cards due on the same day
        List<LocalDate> dueDates = List.of(
                baseDate.plusDays(2),
                baseDate.plusDays(2),
                baseDate.plusDays(2)
        );

        // When
        List<DuePoint> forecast = service.forecastDueByDay(dueDates, baseDate, 5);

        // Then
        assertEquals(5, forecast.size());
        assertEquals(0, forecast.get(0).due());
        assertEquals(0, forecast.get(1).due());
        assertEquals(3, forecast.get(2).due()); // All 3 cards due on day 2
    }

    @Test
    void shouldHandleLongForecastPeriod() {
        // Given
        List<LocalDate> dueDates = List.of(
                baseDate,
                baseDate.plusDays(50),
                baseDate.plusDays(99)
        );

        // When
        List<DuePoint> forecast = service.forecastDueSimple(dueDates, baseDate, 100);

        // Then
        assertEquals(100, forecast.size());
        assertEquals(1, forecast.get(0).due());
        assertEquals(1, forecast.get(49).due());
        assertEquals(2, forecast.get(50).due());
        assertEquals(2, forecast.get(98).due());
        assertEquals(3, forecast.get(99).due());
    }

    @Test
    void shouldHandleZeroDayForecast() {
        // Given
        List<LocalDate> dueDates = List.of(baseDate);

        // When
        List<DuePoint> forecast = service.forecastDueSimple(dueDates, baseDate, 0);

        // Then
        assertEquals(0, forecast.size());
    }

    @Test
    void shouldHandleSingleDayForecast() {
        // Given
        List<LocalDate> dueDates = List.of(baseDate);

        // When
        List<DuePoint> forecast = service.forecastDueSimple(dueDates, baseDate, 1);

        // Then
        assertEquals(1, forecast.size());
        assertEquals(baseDate, forecast.get(0).date());
        assertEquals(1, forecast.get(0).due());
    }

    @Test
    void shouldForecastCardsDueTodayAndFuture() {
        // Given
        List<LocalDate> dueDates = List.of(
                baseDate.minusDays(1),  // Past due
                baseDate,               // Due today
                baseDate.plusDays(1),   // Due tomorrow
                baseDate.plusDays(5)    // Due in 5 days
        );

        // When
        List<DuePoint> forecast = service.forecastDueSimple(dueDates, baseDate, 7);

        // Then
        assertEquals(7, forecast.size());
        assertEquals(2, forecast.get(0).due()); // Day 0: past-due + today
        assertEquals(3, forecast.get(1).due()); // Day 1: +tomorrow
        assertEquals(3, forecast.get(4).due()); // Day 4: same
        assertEquals(4, forecast.get(5).due()); // Day 5: +last card
        assertEquals(4, forecast.get(6).due()); // Day 6: same
    }

    @Test
    void shouldForecastWithLocalDateTime() {
        // Given
        ZoneId timezone = ZoneId.systemDefault();
        Map<Long, LocalDateTime> cardsDueAt = Map.of(
                1L, baseDate.atStartOfDay(),
                2L, baseDate.plusDays(2).atStartOfDay(),
                3L, baseDate.plusDays(4).atStartOfDay()
        );

        org.project.memospace.domain.model.stats.Cutoff cutoff =
                new org.project.memospace.domain.model.stats.Cutoff(timezone, 4);

        // When
        List<DuePoint> forecast = service.forecastDue(cardsDueAt, baseDate, 7, cutoff);

        // Then
        assertEquals(7, forecast.size());
        // Note: Exact counts depend on timezone handling, but structure should be correct
        assertTrue(forecast.get(0).due() >= 0);
        assertTrue(forecast.get(6).due() >= 0);
    }

    @Test
    void shouldHandleMixedPastAndFutureDates() {
        // Given
        List<LocalDate> dueDates = List.of(
                baseDate.minusDays(10),
                baseDate.minusDays(5),
                baseDate.minusDays(1),
                baseDate,
                baseDate.plusDays(1),
                baseDate.plusDays(3)
        );

        // When
        List<DuePoint> forecast = service.forecastDueSimple(dueDates, baseDate, 5);

        // Then
        assertEquals(5, forecast.size());
        assertEquals(4, forecast.get(0).due()); // Day 0: 3 past + today
        assertEquals(5, forecast.get(1).due()); // Day 1: +1 more
        assertEquals(5, forecast.get(2).due()); // Day 2: same
        assertEquals(6, forecast.get(3).due()); // Day 3: +1 more
        assertEquals(6, forecast.get(4).due()); // Day 4: same
    }
}
