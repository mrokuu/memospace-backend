package org.project.memospace.domain.learning.value;

import lombok.Value;

/**
 * Value object representing the ease factor in SM-2 spaced repetition algorithm.
 * Ease factor determines how quickly intervals grow for a card.
 *
 * Constraints:
 * - Minimum ease: 1.3 (SM-2 algorithm constraint)
 * - Default ease: 2.5 (starting value for new cards)
 */
@Value
public class Ease {
    double value;
    public static final double MIN_EASE = 1.3;
    public static final double DEFAULT_EASE = 2.5;

    private Ease(double value) {
        if (value < MIN_EASE) {
            throw new IllegalArgumentException(
                    String.format("Ease factor cannot be less than %.1f (got %.2f)", MIN_EASE, value)
            );
        }
        this.value = value;
    }

    public static Ease of(double value) {
        return new Ease(value);
    }

    public static Ease defaultEase() {
        return new Ease(DEFAULT_EASE);
    }

    /**
     * Adjust ease based on review quality using SM-2 formula.
     * Formula: EF' = EF + (0.1 - (4 - q) * (0.08 + (4 - q) * 0.02))
     */
    public Ease adjust(Quality quality) {
        int q = quality.getValue();
        double adjustment = 0.1 - (4 - q) * (0.08 + (4 - q) * 0.02);
        double newEase = Math.max(MIN_EASE, this.value + adjustment);
        return new Ease(newEase);
    }
}
