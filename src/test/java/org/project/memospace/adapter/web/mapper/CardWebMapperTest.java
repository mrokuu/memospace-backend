package org.project.memospace.adapter.web.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.memospace.adapter.web.dto.CardDto;
import org.project.memospace.domain.model.Card;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CardWebMapperTest {

    private CardWebMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CardWebMapper();
    }

    @Test
    void shouldMapCardToDto() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        UUID noteId = UUID.randomUUID();
        UUID templateId = UUID.randomUUID();
        Card card = new Card(
                1L,               // id
                10L,              // deckId
                "What is 2+2?",   // front
                "4",              // back
                Arrays.asList("math", "basic"), // tags
                2.5,              // easeFactor
                7,                // intervalDays
                3,                // repetitions
                now.plusDays(7),  // dueAt
                now.minusDays(1), // createdAt
                now,              // updatedAt
                noteId,           // noteId
                templateId,       // templateId
                null              // clozeIndex
        );

        // When
        CardDto dto = mapper.toDto(card);

        // Then
        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals(10L, dto.deckId());
        assertEquals("What is 2+2?", dto.front());
        assertEquals("4", dto.back());
        assertEquals(2.5, dto.easeFactor());
        assertEquals(7, dto.intervalDays());
        assertEquals(3, dto.repetitions());
        assertEquals(noteId, dto.noteId());
        assertEquals(templateId, dto.templateId());
        assertTrue(dto.tags().contains("math"));
        assertTrue(dto.tags().contains("basic"));
        assertNotNull(dto.dueAt());
    }

    @Test
    void shouldHandleEmptyOptionalFields() {
        // Given
        Card card = Card.create(1L, "Front", "Back", List.of());

        // When
        CardDto dto = mapper.toDto(card);

        // Then
        assertNotNull(dto);
        assertNotNull(dto.tags());
        assertTrue(dto.tags().isEmpty());
        assertNull(dto.noteId());
        assertNull(dto.templateId());
        assertNull(dto.clozeIndex());
    }

    @Test
    void shouldPreserveClozeIndex() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Card card = new Card(
                1L,               // id
                10L,              // deckId
                "Cloze front",    // front
                "Cloze back",     // back
                List.of(),        // tags
                2.5,              // easeFactor
                1,                // intervalDays
                0,                // repetitions
                now,              // dueAt
                now,              // createdAt
                now,              // updatedAt
                UUID.randomUUID(), // noteId
                UUID.randomUUID(), // templateId
                2                 // cloze index
        );

        // When
        CardDto dto = mapper.toDto(card);

        // Then
        assertEquals(2, dto.clozeIndex());
    }
}
