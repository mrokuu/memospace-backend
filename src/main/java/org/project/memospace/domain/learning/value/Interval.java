package org.project.memospace.domain.learning.value;

import lombok.Value;

/**
 * Value object representing the number of days until the next review.
 * Intervals grow based on ease factor and SM-2 algorithm.
 *
 * Constraints:
 * - Minimum interval: 1 day
 */
@Value
public class Interval {
    int days;
    public static final int MIN_INTERVAL = 1;

    private Interval(int days) {
        if (days < MIN_INTERVAL) {
            throw new IllegalArgumentException(
                    String.format("Interval cannot be less than %d day (got %d)", MIN_INTERVAL, days)
            );
        }
        this.days = days;
    }

    public static Interval of(int days) {
        return new Interval(days);
    }

    public static Interval oneDay() {
        return new Interval(1);
    }

    public static Interval sixDays() {
        return new Interval(6);
    }

    /**
     * Calculate the next interval by multiplying current interval by ease factor.
     */
    public Interval next(Ease ease) {
        int nextDays = (int) Math.round(this.days * ease.getValue());
        return new Interval(Math.max(MIN_INTERVAL, nextDays));
    }

    /**
     * Check if this interval has expired given days elapsed.
     */
    public boolean hasElapsed(int daysElapsed) {
        return daysElapsed >= this.days;
    }
}
