package org.project.memospace.adapter.persistence.jpa.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.memospace.adapter.persistence.jpa.entity.CardEntity;
import org.project.memospace.domain.model.Card;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CardJpaMapperTest {

    private CardJpaMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CardJpaMapper();
    }

    @Test
    void shouldMapDomainCardToEntity() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        UUID noteId = UUID.randomUUID();
        UUID templateId = UUID.randomUUID();
        Card card = new Card(
                1L,
                10L,
                "Front text",
                "Back text",
                Arrays.asList("tag1", "tag2", "tag3"),
                2.5,
                7,
                3,
                now.plusDays(7),
                now.minusDays(1),
                now,
                noteId,
                templateId,
                null
        );

        // When
        CardEntity entity = mapper.toEntity(card);

        // Then
        assertEquals(1L, entity.getId());
        assertEquals(10L, entity.getDeckId());
        assertEquals("Front text", entity.getFront());
        assertEquals("Back text", entity.getBack());
        assertEquals("tag1,tag2,tag3", entity.getTags());
        assertEquals(2.5, entity.getEaseFactor());
        assertEquals(7, entity.getIntervalDays());
        assertEquals(3, entity.getRepetitions());
        assertEquals(noteId, entity.getNoteId());
        assertEquals(templateId, entity.getTemplateId());
        assertNull(entity.getClozeIndex());
    }

    @Test
    void shouldMapEntityToDomainCard() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        UUID noteId = UUID.randomUUID();
        UUID templateId = UUID.randomUUID();
        CardEntity entity = new CardEntity(
                1L,
                10L,
                "Front text",
                "Back text",
                "tag1,tag2,tag3",
                2.5,
                7,
                3,
                now.plusDays(7),
                now.minusDays(1),
                now,
                noteId,
                templateId,
                null
        );

        // When
        Card card = mapper.toDomain(entity);

        // Then
        assertEquals(1L, card.id());
        assertEquals(10L, card.deckId());
        assertEquals("Front text", card.front());
        assertEquals("Back text", card.back());
        assertEquals(3, card.tags().size());
        assertTrue(card.tags().contains("tag1"));
        assertTrue(card.tags().contains("tag2"));
        assertTrue(card.tags().contains("tag3"));
        assertEquals(2.5, card.easeFactor());
        assertEquals(7, card.intervalDays());
        assertEquals(3, card.repetitions());
        assertEquals(noteId, card.noteId());
        assertEquals(templateId, card.templateId());
        assertNull(card.clozeIndex());
    }

    @Test
    void shouldHandleEmptyTags() {
        // Given
        CardEntity entity = new CardEntity(
                1L, 10L, "Front", "Back", "", 2.5, 1, 0,
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(),
                null, null, null
        );

        // When
        Card card = mapper.toDomain(entity);

        // Then
        assertNotNull(card.tags());
        assertTrue(card.tags().isEmpty());
    }

    @Test
    void shouldHandleNullTags() {
        // Given
        CardEntity entity = new CardEntity(
                1L, 10L, "Front", "Back", null, 2.5, 1, 0,
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(),
                null, null, null
        );

        // When
        Card card = mapper.toDomain(entity);

        // Then
        assertNotNull(card.tags());
        assertTrue(card.tags().isEmpty());
    }

    @Test
    void shouldRoundTripCorrectly() {
        // Given
        Card original = Card.create(1L, "Test front", "Test back", Arrays.asList("math", "easy"));

        // When
        CardEntity entity = mapper.toEntity(original);
        Card roundTripped = mapper.toDomain(entity);

        // Then
        assertEquals(original.front(), roundTripped.front());
        assertEquals(original.back(), roundTripped.back());
        assertEquals(original.tags().size(), roundTripped.tags().size());
        assertTrue(roundTripped.tags().containsAll(original.tags()));
    }

    @Test
    void shouldPreserveClozeIndex() {
        // Given
        Card clozeCard = new Card(
                1L, 10L, "Cloze {{c1::text}}", "Answer", List.of(),
                2.5, 1, 0, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(),
                UUID.randomUUID(), UUID.randomUUID(), 1
        );

        // When
        CardEntity entity = mapper.toEntity(clozeCard);
        Card result = mapper.toDomain(entity);

        // Then
        assertEquals(1, result.clozeIndex());
    }
}
