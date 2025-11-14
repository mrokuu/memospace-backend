package org.memospace.learning.value;

/**
 * Constants for default scheduling values in the SM-2 spaced repetition algorithm.
 * <p>
 * These values are used when creating new cards.
 */
public final class SchedulingDefaults {

    /**
     * Default ease factor for new cards (2.5 in SM-2 algorithm).
     */
    public static final double DEFAULT_EASE_FACTOR = Ease.DEFAULT_EASE;

    /**
     * Default interval in days for new cards (1 day).
     */
    public static final int DEFAULT_INTERVAL_DAYS = 1;

    /**
     * Default number of repetitions for new cards (0).
     */
    public static final int DEFAULT_REPETITIONS = 0;

    private SchedulingDefaults() {
        throw new AssertionError("Utility class should not be instantiated");
    }
}
