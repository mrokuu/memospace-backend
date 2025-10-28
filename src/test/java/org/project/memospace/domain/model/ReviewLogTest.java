package org.project.memospace.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReviewLogTest {

    @Test
    void shouldCreateReviewLogWithAllFields() {
        // Given
        Long id = 1L;
        Long cardId = 100L;
        Long deckId = 10L;
        int quality = 4;
        LocalDateTime reviewedAt = LocalDateTime.now();
        Integer msSpent = 5000;
        Double easeFactorAfter = 2.5;
        Integer intervalAfter = 7;

        // When
        ReviewLog log = new ReviewLog(id, cardId, deckId, quality, reviewedAt, msSpent, easeFactorAfter, intervalAfter);

        // Then
        assertEquals(id, log.id());
        assertEquals(cardId, log.cardId());
        assertEquals(deckId, log.deckId());
        assertEquals(quality, log.quality());
        assertEquals(reviewedAt, log.reviewedAt());
        assertEquals(msSpent, log.msSpent());
        assertEquals(easeFactorAfter, log.easeFactorAfter());
        assertEquals(intervalAfter, log.intervalAfter());
    }

    @Test
    void shouldThrowExceptionWhenCardIdIsNull() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When/Then
        assertThrows(NullPointerException.class, () ->
                new ReviewLog(1L, null, 10L, 4, now, 5000, 2.5, 7)
        );
    }

    @Test
    void shouldThrowExceptionWhenReviewedAtIsNull() {
        // When/Then
        assertThrows(NullPointerException.class, () ->
                new ReviewLog(1L, 100L, 10L, 4, null, 5000, 2.5, 7)
        );
    }

    @Test
    void shouldAllowNullDeckId() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        ReviewLog log = new ReviewLog(1L, 100L, null, 4, now, 5000, 2.5, 7);

        // Then
        assertNull(log.deckId());
        assertEquals(100L, log.cardId());
    }

    @Test
    void shouldAllowNullMsSpent() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        ReviewLog log = new ReviewLog(1L, 100L, 10L, 4, now, null, 2.5, 7);

        // Then
        assertNull(log.msSpent());
        assertEquals(100L, log.cardId());
    }

    @Test
    void shouldAllowNullEaseFactorAfter() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        ReviewLog log = new ReviewLog(1L, 100L, 10L, 4, now, 5000, null, 7);

        // Then
        assertNull(log.easeFactorAfter());
        assertEquals(100L, log.cardId());
    }

    @Test
    void shouldAllowNullIntervalAfter() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        ReviewLog log = new ReviewLog(1L, 100L, 10L, 4, now, 5000, 2.5, null);

        // Then
        assertNull(log.intervalAfter());
        assertEquals(100L, log.cardId());
    }

    @Test
    void shouldCreateReviewLogFromReviewResult() {
        // Given
        Long cardId = 100L;
        Long deckId = 10L;
        ReviewResult reviewResult = ReviewResult.builder()
                .quality(4)
                .reviewedAt(LocalDateTime.now())
                .msSpent(5000)
                .build();
        Double easeFactorAfter = 2.6;
        Integer intervalAfter = 8;

        // When
        ReviewLog log = ReviewLog.create(cardId, deckId, reviewResult, easeFactorAfter, intervalAfter);

        // Then
        assertNull(log.id());
        assertEquals(cardId, log.cardId());
        assertEquals(deckId, log.deckId());
        assertEquals(reviewResult.quality(), log.quality());
        assertEquals(reviewResult.reviewedAt(), log.reviewedAt());
        assertEquals(reviewResult.msSpent(), log.msSpent());
        assertEquals(easeFactorAfter, log.easeFactorAfter());
        assertEquals(intervalAfter, log.intervalAfter());
    }

    @Test
    void shouldSupportWithId() {
        // Given
        ReviewLog log = ReviewLog.create(100L, 10L,
                ReviewResult.builder().quality(4).reviewedAt(LocalDateTime.now()).msSpent(5000).build(),
                2.5, 7);
        Long newId = 999L;

        // When
        ReviewLog updatedLog = log.withId(newId);

        // Then
        assertEquals(newId, updatedLog.id());
        assertEquals(log.cardId(), updatedLog.cardId());
    }

    @Test
    void shouldSupportBuilderPattern() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        ReviewLog log = ReviewLog.builder()
                .id(1L)
                .cardId(100L)
                .deckId(10L)
                .quality(4)
                .reviewedAt(now)
                .msSpent(5000)
                .easeFactorAfter(2.5)
                .intervalAfter(7)
                .build();

        // Then
        assertEquals(1L, log.id());
        assertEquals(100L, log.cardId());
        assertEquals(4, log.quality());
    }

    @Test
    void shouldSupportToBuilder() {
        // Given
        ReviewLog original = ReviewLog.create(100L, 10L,
                ReviewResult.builder().quality(4).reviewedAt(LocalDateTime.now()).msSpent(5000).build(),
                2.5, 7);

        // When
        ReviewLog modified = original.toBuilder()
                .id(999L)
                .easeFactorAfter(2.8)
                .build();

        // Then
        assertEquals(999L, modified.id());
        assertEquals(original.cardId(), modified.cardId());
        assertEquals(2.8, modified.easeFactorAfter());
    }

    @Test
    void shouldHandleZeroQuality() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        ReviewLog log = new ReviewLog(1L, 100L, 10L, 0, now, 5000, 2.5, 0);

        // Then
        assertEquals(0, log.quality());
        assertEquals(0, log.intervalAfter());
    }

    @Test
    void shouldHandleHighQuality() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        ReviewLog log = new ReviewLog(1L, 100L, 10L, 4, now, 5000, 2.5, 100);

        // Then
        assertEquals(4, log.quality());
        assertEquals(100, log.intervalAfter());
    }
}
