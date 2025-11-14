package org.memospace.port.stats;

import org.memospace.model.stats.DeckScope;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Read port for card data optimized for statistics queries.
 */
public interface CardStatsReadPort {

    /**
     * Get due dates for all cards in the scope for forecast purposes.
     *
     * @param scope Deck scope (specific deck or all)
     * @return List of due dates
     */
    List<LocalDateTime> getDueDatesForForecast(DeckScope scope);

    /**
     * Count cards due on or before each date.
     * This is a more efficient method if the adapter can optimize the query.
     *
     * @param scope Deck scope
     * @param dates List of dates to count for
     * @return List of counts corresponding to the dates
     */
    default List<Integer> countDueByDates(DeckScope scope, List<LocalDate> dates) {
        // Default implementation using getDueDatesForForecast
        // Adapters can override for better performance
        List<LocalDateTime> dueDates = getDueDatesForForecast(scope);
        return dates.stream()
                .map(date -> {
                    LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
                    return (int) dueDates.stream()
                            .filter(dueDate -> !dueDate.isAfter(endOfDay))
                            .count();
                })
                .toList();
    }
}
