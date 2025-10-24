package org.project.memospace.adapter.web.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.memospace.adapter.web.dto.DeckDto;
import org.project.memospace.domain.model.Deck;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DeckWebMapperTest {

    private DeckWebMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new DeckWebMapper();
    }

    @Test
    void shouldMapDeckToDto() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Deck deck = new Deck(42L, "Spanish", "Spanish vocabulary", now);

        // When
        DeckDto dto = mapper.toDto(deck);

        // Then
        assertEquals(42L, dto.id());
        assertEquals("Spanish", dto.name());
        assertEquals("Spanish vocabulary", dto.description());
        assertEquals(now, dto.createdAt());
    }

    @Test
    void shouldHandleNullDescription() {
        // Given
        Deck deck = Deck.create("Math", null).withId(1L);

        // When
        DeckDto dto = mapper.toDto(deck);

        // Then
        assertEquals(1L, dto.id());
        assertEquals("Math", dto.name());
        assertNull(dto.description());
        assertNotNull(dto.createdAt());
    }

    @Test
    void shouldHandleNullId() {
        // Given
        Deck deck = Deck.create("New Deck", "Not persisted yet");

        // When
        DeckDto dto = mapper.toDto(deck);

        // Then
        assertNull(dto.id());
        assertEquals("New Deck", dto.name());
        assertEquals("Not persisted yet", dto.description());
    }
}
