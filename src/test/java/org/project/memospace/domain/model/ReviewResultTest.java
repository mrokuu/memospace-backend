package org.project.memospace.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReviewResultTest {

    @Test
    void shouldCreateReviewResultWithValidQuality() {
        for (int quality = 0; quality <= 4; quality++) {
            ReviewResult result = ReviewResult.create(quality, 5000);
            assertEquals(quality, result.quality());
            assertEquals(5000, result.msSpent());
            assertNotNull(result.reviewedAt());
        }
    }

    @Test
    void shouldCreateReviewResultWithoutMsSpent() {
        ReviewResult result = ReviewResult.create(3);
        assertEquals(3, result.quality());
        assertNull(result.msSpent());
        assertNotNull(result.reviewedAt());
    }

    @Test
    void shouldThrowExceptionForInvalidQualityTooLow() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ReviewResult.create(-1, 1000)
        );
        assertEquals("Quality must be between 0 and 4, but was: -1", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForInvalidQualityTooHigh() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ReviewResult.create(5, 1000)
        );
        assertEquals("Quality must be between 0 and 4, but was: 5", exception.getMessage());
    }

    @Test
    void shouldRequireNonNullReviewedAt() {
        assertThrows(NullPointerException.class, () ->
                new ReviewResult(3, null, 1000));
    }
}