package org.memospace.learning.value;

/**
 * Value object representing the ease factor in SM-2 spaced repetition algorithm.
 * Ease factor determines how quickly intervals grow for a card.
 * <p>
 * Constraints:
 * - Minimum ease: 1.3 (SM-2 algorithm constraint)
 * - Default ease: 2.5 (starting value for new cards)
 */
public record Ease(double value) {
    public static final double MIN_EASE = 1.3;
    public static final double DEFAULT_EASE = 2.5;

    public Ease {
        if (value < MIN_EASE) {
            throw new IllegalArgumentException(
                    String.format("Ease factor cannot be less than %.1f (got %.2f)", MIN_EASE, value)
            );
        }
    }

    public static Ease of(double value) {
        return new Ease(value);
    }

    public static Ease defaultEase() {
        return new Ease(DEFAULT_EASE);
    }
}
