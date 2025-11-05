package org.project.memospace.domain.learning.value;

import lombok.Value;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Value object representing when a card is due for review.
 */
@Value
public class DueDate {
    LocalDateTime value;

    private DueDate(LocalDateTime value) {
        this.value = Objects.requireNonNull(value, "Due date cannot be null");
    }

    public static DueDate of(LocalDateTime value) {
        return new DueDate(value);
    }

    public static DueDate now() {
        return new DueDate(LocalDateTime.now());
    }

    public static DueDate afterDays(int days) {
        return new DueDate(LocalDateTime.now().plusDays(days));
    }

    public static DueDate after(Interval interval) {
        return afterDays(interval.getDays());
    }

    /**
     * Check if this card is due now (or overdue).
     */
    public boolean isDue() {
        return isDueAt(LocalDateTime.now());
    }

    /**
     * Check if this card is due at a specific time.
     */
    public boolean isDueAt(LocalDateTime time) {
        return !value.isAfter(time);
    }

    /**
     * Check if this card is overdue by a certain threshold.
     */
    public boolean isOverdueBy(int days) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(days);
        return value.isBefore(threshold);
    }
}
