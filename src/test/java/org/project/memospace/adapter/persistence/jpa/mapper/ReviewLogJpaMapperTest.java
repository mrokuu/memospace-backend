package org.project.memospace.adapter.persistence.jpa.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.memospace.adapter.persistence.jpa.entity.ReviewLogEntity;
import org.project.memospace.domain.model.ReviewLog;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReviewLogJpaMapperTest {

    private ReviewLogJpaMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ReviewLogJpaMapper();
    }

    @Test
    void shouldMapDomainToEntity() {
        // Given
        ReviewLog domain = ReviewLog.builder()
                .id(1L)
                .cardId(100L)
                .deckId(10L)
                .quality(4)
                .reviewedAt(LocalDateTime.of(2025, 1, 1, 12, 0))
                .msSpent(5000)
                .easeFactorAfter(2.5)
                .intervalAfter(7)
                .build();

        // When
        ReviewLogEntity entity = mapper.toEntity(domain);

        // Then
        assertEquals(domain.id(), entity.getId());
        assertEquals(domain.cardId(), entity.getCardId());
        assertEquals(domain.deckId(), entity.getDeckId());
        assertEquals(domain.quality(), entity.getQuality());
        assertEquals(domain.reviewedAt(), entity.getReviewedAt());
        assertEquals(domain.msSpent(), entity.getMsSpent());
        assertEquals(domain.easeFactorAfter(), entity.getEaseFactorAfter());
        assertEquals(domain.intervalAfter(), entity.getIntervalAfter());
    }

    @Test
    void shouldMapEntityToDomain() {
        // Given
        ReviewLogEntity entity = new ReviewLogEntity(
                2L, 200L, 20L, 3, LocalDateTime.of(2025, 1, 2, 14, 30),
                4500, 2.3, 5
        );

        // When
        ReviewLog domain = mapper.toDomain(entity);

        // Then
        assertEquals(entity.getId(), domain.id());
        assertEquals(entity.getCardId(), domain.cardId());
        assertEquals(entity.getDeckId(), domain.deckId());
        assertEquals(entity.getQuality(), domain.quality());
        assertEquals(entity.getReviewedAt(), domain.reviewedAt());
        assertEquals(entity.getMsSpent(), domain.msSpent());
        assertEquals(entity.getEaseFactorAfter(), domain.easeFactorAfter());
        assertEquals(entity.getIntervalAfter(), domain.intervalAfter());
    }

    @Test
    void shouldMapReviewLogWithNullOptionalFields() {
        // Given
        ReviewLog domain = ReviewLog.builder()
                .id(3L)
                .cardId(300L)
                .deckId(null)
                .quality(2)
                .reviewedAt(LocalDateTime.now())
                .msSpent(null)
                .easeFactorAfter(null)
                .intervalAfter(null)
                .build();

        // When
        ReviewLogEntity entity = mapper.toEntity(domain);

        // Then
        assertEquals(domain.cardId(), entity.getCardId());
        assertNull(entity.getDeckId());
        assertNull(entity.getMsSpent());
        assertNull(entity.getEaseFactorAfter());
        assertNull(entity.getIntervalAfter());
    }

    @Test
    void shouldMapReviewLogWithZeroQuality() {
        // Given
        ReviewLog domain = ReviewLog.builder()
                .id(4L)
                .cardId(400L)
                .deckId(40L)
                .quality(0)
                .reviewedAt(LocalDateTime.now())
                .msSpent(1000)
                .easeFactorAfter(2.5)
                .intervalAfter(0)
                .build();

        // When
        ReviewLogEntity entity = mapper.toEntity(domain);

        // Then
        assertEquals(0, entity.getQuality());
        assertEquals(0, entity.getIntervalAfter());
    }

    @Test
    void shouldMapReviewLogWithMaxQuality() {
        // Given
        ReviewLog domain = ReviewLog.builder()
                .id(5L)
                .cardId(500L)
                .deckId(50L)
                .quality(4)
                .reviewedAt(LocalDateTime.now())
                .msSpent(3000)
                .easeFactorAfter(2.8)
                .intervalAfter(30)
                .build();

        // When
        ReviewLogEntity entity = mapper.toEntity(domain);

        // Then
        assertEquals(4, entity.getQuality());
        assertEquals(30, entity.getIntervalAfter());
    }

    @Test
    void shouldRoundTripDomainToEntityToDomain() {
        // Given
        ReviewLog original = ReviewLog.builder()
                .id(6L)
                .cardId(600L)
                .deckId(60L)
                .quality(3)
                .reviewedAt(LocalDateTime.of(2025, 1, 15, 10, 0))
                .msSpent(2500)
                .easeFactorAfter(2.4)
                .intervalAfter(10)
                .build();

        // When
        ReviewLogEntity entity = mapper.toEntity(original);
        ReviewLog roundTripped = mapper.toDomain(entity);

        // Then
        assertEquals(original.id(), roundTripped.id());
        assertEquals(original.cardId(), roundTripped.cardId());
        assertEquals(original.deckId(), roundTripped.deckId());
        assertEquals(original.quality(), roundTripped.quality());
        assertEquals(original.reviewedAt(), roundTripped.reviewedAt());
        assertEquals(original.msSpent(), roundTripped.msSpent());
        assertEquals(original.easeFactorAfter(), roundTripped.easeFactorAfter());
        assertEquals(original.intervalAfter(), roundTripped.intervalAfter());
    }

    @Test
    void shouldRoundTripEntityToDomainToEntity() {
        // Given
        ReviewLogEntity original = new ReviewLogEntity(
                7L, 700L, 70L, 4, LocalDateTime.of(2025, 1, 20, 9, 30),
                5500, 2.6, 15
        );

        // When
        ReviewLog domain = mapper.toDomain(original);
        ReviewLogEntity roundTripped = mapper.toEntity(domain);

        // Then
        assertEquals(original.getId(), roundTripped.getId());
        assertEquals(original.getCardId(), roundTripped.getCardId());
        assertEquals(original.getDeckId(), roundTripped.getDeckId());
        assertEquals(original.getQuality(), roundTripped.getQuality());
        assertEquals(original.getReviewedAt(), roundTripped.getReviewedAt());
        assertEquals(original.getMsSpent(), roundTripped.getMsSpent());
        assertEquals(original.getEaseFactorAfter(), roundTripped.getEaseFactorAfter());
        assertEquals(original.getIntervalAfter(), roundTripped.getIntervalAfter());
    }

    @Test
    void shouldMapReviewLogWithNullId() {
        // Given
        ReviewLog domain = ReviewLog.builder()
                .id(null)
                .cardId(800L)
                .deckId(80L)
                .quality(3)
                .reviewedAt(LocalDateTime.now())
                .msSpent(4000)
                .easeFactorAfter(2.5)
                .intervalAfter(12)
                .build();

        // When
        ReviewLogEntity entity = mapper.toEntity(domain);

        // Then
        assertNull(entity.getId());
        assertEquals(domain.cardId(), entity.getCardId());
    }

    @Test
    void shouldMapReviewLogWithLargeInterval() {
        // Given
        ReviewLog domain = ReviewLog.builder()
                .id(9L)
                .cardId(900L)
                .deckId(90L)
                .quality(4)
                .reviewedAt(LocalDateTime.now())
                .msSpent(2000)
                .easeFactorAfter(2.9)
                .intervalAfter(365)
                .build();

        // When
        ReviewLogEntity entity = mapper.toEntity(domain);

        // Then
        assertEquals(365, entity.getIntervalAfter());
    }

    @Test
    void shouldMapReviewLogWithLargeTimeSpent() {
        // Given
        ReviewLog domain = ReviewLog.builder()
                .id(10L)
                .cardId(1000L)
                .deckId(100L)
                .quality(3)
                .reviewedAt(LocalDateTime.now())
                .msSpent(999999)
                .easeFactorAfter(2.5)
                .intervalAfter(7)
                .build();

        // When
        ReviewLogEntity entity = mapper.toEntity(domain);

        // Then
        assertEquals(999999, entity.getMsSpent());
    }
}
