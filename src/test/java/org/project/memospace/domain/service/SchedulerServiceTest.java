package org.project.memospace.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.memospace.domain.model.Card;
import org.project.memospace.domain.model.ReviewResult;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SchedulerServiceTest {

    private SchedulerService schedulerService;
    private Card newCard;

    @BeforeEach
    void setUp() {
        schedulerService = new SchedulerService();
        newCard = Card.create(1L, "What is 2+2?", "4", List.of("math"));
    }

    @Test
    void shouldResetCardOnFailedReview() {
        // Given
        Card progressedCard = newCard.updateScheduling(2.7, 10, 3, LocalDateTime.now().minusDays(1));
        ReviewResult failedReview = ReviewResult.create(2); // Quality < 3 = failed

        // When
        Card updatedCard = schedulerService.updateCardScheduling(progressedCard, failedReview);

        // Then
        assertEquals(0, updatedCard.repetitions());
        assertEquals(1, updatedCard.intervalDays());
        assertEquals(2.5, updatedCard.easeFactor());
        assertTrue(updatedCard.dueAt().isAfter(LocalDateTime.now()));
        assertTrue(updatedCard.dueAt().isBefore(LocalDateTime.now().plusDays(2)));
    }

    @Test
    void shouldSetIntervalTo1DayForFirstSuccessfulReview() {
        // Given
        ReviewResult goodReview = ReviewResult.create(3);

        // When
        Card updatedCard = schedulerService.updateCardScheduling(newCard, goodReview);

        // Then
        assertEquals(1, updatedCard.repetitions());
        assertEquals(1, updatedCard.intervalDays());
        assertTrue(updatedCard.dueAt().isAfter(LocalDateTime.now()));
        assertTrue(updatedCard.dueAt().isBefore(LocalDateTime.now().plusDays(2)));
    }

    @Test
    void shouldSetIntervalTo6DaysForSecondSuccessfulReview() {
        // Given
        Card cardWith1Rep = newCard.updateScheduling(2.5, 1, 1, LocalDateTime.now().minusDays(1));
        ReviewResult goodReview = ReviewResult.create(3);

        // When
        Card updatedCard = schedulerService.updateCardScheduling(cardWith1Rep, goodReview);

        // Then
        assertEquals(2, updatedCard.repetitions());
        assertEquals(6, updatedCard.intervalDays());
        assertTrue(updatedCard.dueAt().isAfter(LocalDateTime.now().plusDays(5)));
        assertTrue(updatedCard.dueAt().isBefore(LocalDateTime.now().plusDays(7)));
    }

    @Test
    void shouldUseEaseFactorForSubsequentReviews() {
        // Given
        Card experiencedCard = newCard.updateScheduling(2.5, 6, 2, LocalDateTime.now().minusDays(1));
        ReviewResult goodReview = ReviewResult.create(3);

        // When
        Card updatedCard = schedulerService.updateCardScheduling(experiencedCard, goodReview);

        // Then
        assertEquals(3, updatedCard.repetitions());
        assertEquals(Math.round(6 * 2.5), updatedCard.intervalDays()); // 6 * 2.5 = 15
    }

    @Test
    void shouldUpdateEaseFactorBasedOnQuality() {
        // Test various quality levels and their effect on ease factor
        Card testCard = newCard.updateScheduling(2.5, 1, 1, LocalDateTime.now().minusDays(1));

        // Quality 3: ease factor should stay the same (2.5)
        ReviewResult quality3 = ReviewResult.create(3);
        Card updated3 = schedulerService.updateCardScheduling(testCard, quality3);
        assertEquals(2.5, updated3.easeFactor(), 0.001);

        // Quality 4: ease factor should increase
        ReviewResult quality4 = ReviewResult.create(4);
        Card updated4 = schedulerService.updateCardScheduling(testCard, quality4);
        assertTrue(updated4.easeFactor() > 2.5);
    }

    @Test
    void shouldNotLetEaseFactorGoBelowMinimum() {
        // Given
        Card lowEaseCard = newCard.updateScheduling(1.3, 1, 1, LocalDateTime.now().minusDays(1));
        ReviewResult poorReview = ReviewResult.create(3); // This would normally lower ease factor

        // When
        Card updatedCard = schedulerService.updateCardScheduling(lowEaseCard, poorReview);

        // Then
        assertTrue(updatedCard.easeFactor() >= 1.3);
    }

    @Test
    void shouldCalculateCorrectEaseFactorForDifferentQualities() {
        Card testCard = newCard.updateScheduling(2.5, 1, 1, LocalDateTime.now().minusDays(1));

        // Test quality 0 (complete blackout)
        ReviewResult quality0 = ReviewResult.create(0);
        Card updated0 = schedulerService.updateCardScheduling(testCard, quality0);
        assertEquals(0, updated0.repetitions()); // Should reset
        assertEquals(2.5, updated0.easeFactor()); // Reset to default

        // Test quality 1 (incorrect, easy recall)
        ReviewResult quality1 = ReviewResult.create(1);
        Card updated1 = schedulerService.updateCardScheduling(testCard, quality1);
        assertEquals(0, updated1.repetitions()); // Should reset
        assertEquals(2.5, updated1.easeFactor()); // Reset to default

        // Test quality 2 (incorrect, difficult recall)
        ReviewResult quality2 = ReviewResult.create(2);
        Card updated2 = schedulerService.updateCardScheduling(testCard, quality2);
        assertEquals(0, updated2.repetitions()); // Should reset
        assertEquals(2.5, updated2.easeFactor()); // Reset to default

        // Test quality 4 (perfect response)
        ReviewResult quality4 = ReviewResult.create(4);
        Card updated4 = schedulerService.updateCardScheduling(testCard, quality4);
        assertEquals(2, updated4.repetitions());
        assertTrue(updated4.easeFactor() > 2.5); // Should increase
    }
}