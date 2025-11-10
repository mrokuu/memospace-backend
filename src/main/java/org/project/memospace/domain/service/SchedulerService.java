package org.project.memospace.domain.service;

import org.project.memospace.domain.learning.value.Ease;
import org.project.memospace.domain.learning.value.SchedulingDefaults;
import org.project.memospace.domain.model.Card;
import org.project.memospace.domain.model.ReviewResult;

import java.time.LocalDateTime;

/**
 * Service implementing the SM-2 spaced repetition algorithm for card scheduling.
 * <p>
 * SM-2 algorithm constants:
 * - First repetition interval: 1 day
 * - Second repetition interval: 6 days
 * - Subsequent intervals: previous_interval * ease_factor
 */
public class SchedulerService {

    private static final int FIRST_REPETITION_INTERVAL = 1;  // days
    private static final int SECOND_REPETITION_INTERVAL = 6; // days
    private static final int PASSING_QUALITY_THRESHOLD = 3;

    public Card updateCardScheduling(Card card, ReviewResult reviewResult) {
        int quality = reviewResult.quality();

        if (quality < PASSING_QUALITY_THRESHOLD) {
            // Failed review: reset to beginning
            return card.updateScheduling(
                    SchedulingDefaults.DEFAULT_EASE_FACTOR,
                    SchedulingDefaults.DEFAULT_INTERVAL_DAYS,
                    SchedulingDefaults.DEFAULT_REPETITIONS,
                    LocalDateTime.now().plusDays(FIRST_REPETITION_INTERVAL)
            );
        }

        // Successful review
        int newRepetitions = card.repetitions() + 1;
        int newIntervalDays;

        if (newRepetitions == 1) {
            newIntervalDays = FIRST_REPETITION_INTERVAL;
        } else if (newRepetitions == 2) {
            newIntervalDays = SECOND_REPETITION_INTERVAL;
        } else {
            newIntervalDays = (int) Math.round(card.intervalDays() * card.easeFactor());
        }

        // Update ease factor using SM-2 algorithm formula
        double newEaseFactor = Math.max(
                Ease.MIN_EASE,
                card.easeFactor() + (0.1 - (4 - quality) * (0.08 + (4 - quality) * 0.02))
        );

        LocalDateTime newDueAt = LocalDateTime.now().plusDays(newIntervalDays);

        return card.updateScheduling(newEaseFactor, newIntervalDays, newRepetitions, newDueAt);
    }
}