package org.project.memospace.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

    @Test
    void shouldCreateDeckWithDefaults() {
        // When
        Deck deck = Deck.create("Spanish", "Spanish vocabulary");

        // Then
        assertNull(deck.id()); // ID not assigned yet
        assertEquals("Spanish", deck.name());
        assertEquals("Spanish vocabulary", deck.description());
        assertNotNull(deck.createdAt());
    }

    @Test
    void shouldCreateDeckWithNullDescription() {
        // When
        Deck deck = Deck.create("Math", null);

        // Then
        assertEquals("Math", deck.name());
        assertNull(deck.description());
    }

    @Test
    void shouldRejectNullName() {
        // When / Then
        assertThrows(NullPointerException.class, () ->
                Deck.create(null, "Description")
        );
    }

    @Test
    void shouldUpdateDeckDetails() {
        // Given
        Deck original = Deck.create("Spanish", "Old description");

        // When
        Deck updated = original.updateDetails("French", "New description");

        // Then
        assertEquals("French", updated.name());
        assertEquals("New description", updated.description());
        assertEquals(original.createdAt(), updated.createdAt()); // Preserved
    }

    @Test
    void shouldSetIdWithWith() {
        // Given
        Deck deck = Deck.create("Test", null);

        // When
        Deck withId = deck.withId(42L);

        // Then
        assertEquals(42L, withId.id());
        assertEquals(deck.name(), withId.name());
    }
}
