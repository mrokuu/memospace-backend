package org.memospace.model.stats;

import lombok.Builder;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents the number of cards due on a specific date.
 */
@Builder(toBuilder = true)
public record DuePoint(LocalDate date, int due) {
    public DuePoint(LocalDate date, int due) {
        this.date = Objects.requireNonNull(date);
        this.due = due;
    }
}
