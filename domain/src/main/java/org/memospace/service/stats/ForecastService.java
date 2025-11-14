package org.memospace.service.stats;

import org.memospace.model.stats.Cutoff;
import org.memospace.model.stats.DuePoint;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Domain service for forecasting due cards.
 */
public class ForecastService {

    /**
     * Forecast due cards for the next N days.
     *
     * @param cardsDueAt Map of card IDs to their due dates
     * @param from Starting date for forecast (typically today)
     * @param days Number of days to forecast
     * @param cutoff Cutoff configuration for day boundaries
     * @return List of due points
     */
    public List<DuePoint> forecastDue(Map<Long, LocalDateTime> cardsDueAt, LocalDate from, int days, Cutoff cutoff) {
        List<DuePoint> forecast = new ArrayList<>();

        for (int i = 0; i < days; i++) {
            LocalDate date = from.plusDays(i);
            LocalDate nextDay = date.plusDays(1);

            // Count cards due before the start of the next day
            long dueCount = cardsDueAt.values().stream()
                    .filter(dueAt -> {
                        LocalDate dueDate = cutoff.toLocalDate(
                                dueAt.atZone(cutoff.timezone()).toInstant()
                        );
                        return !dueDate.isAfter(date);
                    })
                    .count();

            forecast.add(new DuePoint(date, (int) dueCount));
        }

        return forecast;
    }

    /**
     * Simplified forecast using only LocalDate due dates.
     */
    public List<DuePoint> forecastDueSimple(List<LocalDate> cardDueDates, LocalDate from, int days) {
        List<DuePoint> forecast = new ArrayList<>();

        for (int i = 0; i < days; i++) {
            LocalDate date = from.plusDays(i);

            long dueCount = cardDueDates.stream()
                    .filter(dueDate -> !dueDate.isAfter(date))
                    .count();

            forecast.add(new DuePoint(date, (int) dueCount));
        }

        return forecast;
    }

    /**
     * Count cards due for each day in the forecast period.
     * This counts cards that are due ON that specific day (not cumulative).
     */
    public List<DuePoint> forecastDueByDay(List<LocalDate> cardDueDates, LocalDate from, int days) {
        // Group cards by due date
        Map<LocalDate, Long> countByDate = cardDueDates.stream()
                .collect(Collectors.groupingBy(d -> d, Collectors.counting()));

        List<DuePoint> forecast = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            LocalDate date = from.plusDays(i);
            int count = countByDate.getOrDefault(date, 0L).intValue();
            forecast.add(new DuePoint(date, count));
        }

        return forecast;
    }
}
