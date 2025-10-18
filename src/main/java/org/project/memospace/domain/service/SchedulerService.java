package org.project.memospace.domain.service;

import org.project.memospace.domain.model.Card;
import org.project.memospace.domain.model.ReviewResult;

import java.time.LocalDateTime;

public class SchedulerService {

    public Card updateCardScheduling(Card card, ReviewResult reviewResult) {
        int quality = reviewResult.quality();

        if (quality < 3) {
            // Failed review: reset to beginning
            return card.updateScheduling(2.5, 1, 0, LocalDateTime.now().plusDays(1));
        }

        // Successful review
        int newRepetitions = card.repetitions() + 1;
        int newIntervalDays;

        if (newRepetitions == 1) {
            newIntervalDays = 1;
        } else if (newRepetitions == 2) {
            newIntervalDays = 6;
        } else {
            newIntervalDays = (int) Math.round(card.intervalDays() * card.easeFactor());
        }

        // Update ease factor using SM-2 algorithm
        double newEaseFactor = Math.max(1.3,
            card.easeFactor() + (0.1 - (4 - quality) * (0.08 + (4 - quality) * 0.02)));

        LocalDateTime newDueAt = LocalDateTime.now().plusDays(newIntervalDays);

        return card.updateScheduling(newEaseFactor, newIntervalDays, newRepetitions, newDueAt);
    }
}