package org.memospace.model.stats;

import lombok.Builder;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Represents a time window for statistics queries.
 * Defined in local dates with a closed-open range [from, to).
 */
@Builder(toBuilder = true)
public record Window(LocalDate fromInclusive, LocalDate toExclusive) {
    public Window(LocalDate fromInclusive, LocalDate toExclusive) {
        this.fromInclusive = Objects.requireNonNull(fromInclusive, "From date cannot be null");
        this.toExclusive = Objects.requireNonNull(toExclusive, "To date cannot be null");
        if (!toExclusive.isAfter(fromInclusive)) {
            throw new IllegalArgumentException("To date must be after from date");
        }
    }

    public static Window fromDays(int days, Cutoff cutoff, Clock clock) {
        LocalDate today = cutoff.today(clock);
        LocalDate from = today.minusDays(days - 1);
        LocalDate to = today.plusDays(1);
        return new Window(from, to);
    }

    public static Window all(LocalDate earliestDate, Cutoff cutoff, Clock clock) {
        LocalDate today = cutoff.today(clock);
        return new Window(earliestDate, today.plusDays(1));
    }

    /**
     * Convert this window to Instant range using the cutoff.
     */
    public InstantRange toInstantRange(Cutoff cutoff) {
        Instant from = cutoff.toInstantAtDayStart(fromInclusive);
        Instant to = cutoff.toInstantAtDayStart(toExclusive);
        return new InstantRange(from, to);
    }

    public long getDays() {
        return ChronoUnit.DAYS.between(fromInclusive, toExclusive);
    }

    /**
         * Helper class for Instant range.
         */
        @Builder(toBuilder = true)
        public record InstantRange(Instant fromInclusive, Instant toExclusive) {
    }
}
