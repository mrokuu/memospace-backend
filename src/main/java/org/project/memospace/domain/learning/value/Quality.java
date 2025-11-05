package org.project.memospace.domain.learning.value;

import lombok.Value;

/**
 * Value object representing the quality of a review answer in SM-2 algorithm.
 *
 * Quality scale:
 * - 0: Complete blackout (total failure to recall)
 * - 1: Incorrect response, but correct answer seemed familiar
 * - 2: Incorrect response, but correct answer was easy to recall
 * - 3: Correct response, but required significant difficulty
 * - 4: Perfect recall with ease
 *
 * Quality < 3 is considered a "lapse" and resets the card's interval.
 * Quality >= 3 is considered successful and increases the interval.
 */
@Value
public class Quality {
    int value;
    public static final int MIN_QUALITY = 0;
    public static final int MAX_QUALITY = 4;
    public static final int PASS_THRESHOLD = 3;

    private Quality(int value) {
        if (value < MIN_QUALITY || value > MAX_QUALITY) {
            throw new IllegalArgumentException(
                    String.format("Quality must be between %d and %d (got %d)",
                            MIN_QUALITY, MAX_QUALITY, value)
            );
        }
        this.value = value;
    }

    public static Quality of(int value) {
        return new Quality(value);
    }

    public boolean isPassing() {
        return value >= PASS_THRESHOLD;
    }

    public boolean isFailing() {
        return value < PASS_THRESHOLD;
    }
}
