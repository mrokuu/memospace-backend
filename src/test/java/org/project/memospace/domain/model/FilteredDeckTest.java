package org.project.memospace.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FilteredDeckTest {

    @Test
    void shouldCreateFilteredDeckWithAllFields() {
        // Given
        UUID id = UUID.randomUUID();
        String name = "Test Filtered Deck";
        UUID ownerDeckId = UUID.randomUUID();
        String query = "is:due";
        int limit = 20;
        FilteredMode mode = FilteredMode.REVIEW_ONLY;
        ReturnPolicy returnPolicy = ReturnPolicy.WHEN_EMPTY;
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(3600);
        Instant lastBuiltAt = now;

        // When
        FilteredDeck deck = new FilteredDeck(id, name, ownerDeckId, query, limit, mode, returnPolicy, now, expiresAt, lastBuiltAt);

        // Then
        assertEquals(id, deck.id());
        assertEquals(name, deck.name());
        assertEquals(ownerDeckId, deck.ownerDeckId());
        assertEquals(query, deck.query());
        assertEquals(limit, deck.limit());
        assertEquals(mode, deck.mode());
        assertEquals(returnPolicy, deck.returnPolicy());
        assertEquals(now, deck.createdAt());
        assertEquals(expiresAt, deck.expiresAt());
        assertEquals(lastBuiltAt, deck.lastBuiltAt());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        // Given
        Instant now = Instant.now();

        // When/Then
        assertThrows(NullPointerException.class, () ->
                new FilteredDeck(UUID.randomUUID(), null, UUID.randomUUID(), "is:due", 20,
                        FilteredMode.REVIEW_ONLY, ReturnPolicy.WHEN_EMPTY, now, null, null)
        );
    }

    @Test
    void shouldThrowExceptionWhenQueryIsNull() {
        // Given
        Instant now = Instant.now();

        // When/Then
        assertThrows(NullPointerException.class, () ->
                new FilteredDeck(UUID.randomUUID(), "Test", UUID.randomUUID(), null, 20,
                        FilteredMode.REVIEW_ONLY, ReturnPolicy.WHEN_EMPTY, now, null, null)
        );
    }

    @Test
    void shouldThrowExceptionWhenModeIsNull() {
        // Given
        Instant now = Instant.now();

        // When/Then
        assertThrows(NullPointerException.class, () ->
                new FilteredDeck(UUID.randomUUID(), "Test", UUID.randomUUID(), "is:due", 20,
                        null, ReturnPolicy.WHEN_EMPTY, now, null, null)
        );
    }

    @Test
    void shouldThrowExceptionWhenReturnPolicyIsNull() {
        // Given
        Instant now = Instant.now();

        // When/Then
        assertThrows(NullPointerException.class, () ->
                new FilteredDeck(UUID.randomUUID(), "Test", UUID.randomUUID(), "is:due", 20,
                        FilteredMode.REVIEW_ONLY, null, now, null, null)
        );
    }

    @Test
    void shouldThrowExceptionWhenCreatedAtIsNull() {
        // When/Then
        assertThrows(NullPointerException.class, () ->
                new FilteredDeck(UUID.randomUUID(), "Test", UUID.randomUUID(), "is:due", 20,
                        FilteredMode.REVIEW_ONLY, ReturnPolicy.WHEN_EMPTY, null, null, null)
        );
    }

    @Test
    void shouldCreateFilteredDeckWithFactoryMethod() {
        // Given
        String name = "Custom Name";
        UUID ownerDeckId = UUID.randomUUID();
        String query = "deck:Math";
        int limit = 50;
        FilteredMode mode = FilteredMode.INCLUDE_NEW;
        ReturnPolicy returnPolicy = ReturnPolicy.AT_CUTOFF;
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(7200);

        // When
        FilteredDeck deck = FilteredDeck.create(name, ownerDeckId, query, limit, mode, returnPolicy, expiresAt, now);

        // Then
        assertNotNull(deck.id());
        assertEquals(name, deck.name());
        assertEquals(ownerDeckId, deck.ownerDeckId());
        assertEquals(query, deck.query());
        assertEquals(limit, deck.limit());
        assertEquals(mode, deck.mode());
        assertEquals(returnPolicy, deck.returnPolicy());
        assertEquals(now, deck.createdAt());
        assertEquals(expiresAt, deck.expiresAt());
        assertNull(deck.lastBuiltAt());
    }

    @Test
    void shouldGenerateDefaultNameWhenNameIsNull() {
        // Given
        UUID ownerDeckId = UUID.randomUUID();
        Instant now = Instant.now();

        // When
        FilteredDeck deck = FilteredDeck.create(null, ownerDeckId, "is:due", 20,
                FilteredMode.REVIEW_ONLY, ReturnPolicy.WHEN_EMPTY, null, now);

        // Then
        assertTrue(deck.name().startsWith("Filtered "));
    }

    @Test
    void shouldMarkDeckAsRebuilt() {
        // Given
        Instant createdAt = Instant.now();
        FilteredDeck originalDeck = FilteredDeck.create("Test", UUID.randomUUID(), "is:due", 20,
                FilteredMode.REVIEW_ONLY, ReturnPolicy.WHEN_EMPTY, null, createdAt);
        Instant rebuildTime = createdAt.plusSeconds(100);

        // When
        FilteredDeck rebuiltDeck = originalDeck.markRebuilt(rebuildTime);

        // Then
        assertEquals(rebuildTime, rebuiltDeck.lastBuiltAt());
        assertEquals(originalDeck.id(), rebuiltDeck.id());
        assertEquals(originalDeck.name(), rebuiltDeck.name());
    }

    @Test
    void shouldReturnFalseWhenExpiresAtIsNull() {
        // Given
        Instant now = Instant.now();
        FilteredDeck deck = FilteredDeck.create("Test", UUID.randomUUID(), "is:due", 20,
                FilteredMode.REVIEW_ONLY, ReturnPolicy.WHEN_EMPTY, null, now);

        // When
        boolean expired = deck.isExpired(now.plusSeconds(10000));

        // Then
        assertFalse(expired);
    }

    @Test
    void shouldReturnFalseWhenNotExpired() {
        // Given
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(3600);
        FilteredDeck deck = FilteredDeck.create("Test", UUID.randomUUID(), "is:due", 20,
                FilteredMode.REVIEW_ONLY, ReturnPolicy.WHEN_EMPTY, expiresAt, now);

        // When
        boolean expired = deck.isExpired(now.plusSeconds(1800));

        // Then
        assertFalse(expired);
    }

    @Test
    void shouldReturnTrueWhenExpired() {
        // Given
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(3600);
        FilteredDeck deck = FilteredDeck.create("Test", UUID.randomUUID(), "is:due", 20,
                FilteredMode.REVIEW_ONLY, ReturnPolicy.WHEN_EMPTY, expiresAt, now);

        // When
        boolean expired = deck.isExpired(now.plusSeconds(7200));

        // Then
        assertTrue(expired);
    }

    @Test
    void shouldSupportWithId() {
        // Given
        FilteredDeck deck = FilteredDeck.create("Test", UUID.randomUUID(), "is:due", 20,
                FilteredMode.REVIEW_ONLY, ReturnPolicy.WHEN_EMPTY, null, Instant.now());
        UUID newId = UUID.randomUUID();

        // When
        FilteredDeck updatedDeck = deck.withId(newId);

        // Then
        assertEquals(newId, updatedDeck.id());
        assertEquals(deck.name(), updatedDeck.name());
    }

    @Test
    void shouldSupportBuilderPattern() {
        // Given
        Instant now = Instant.now();

        // When
        FilteredDeck deck = FilteredDeck.builder()
                .id(UUID.randomUUID())
                .name("Builder Test")
                .ownerDeckId(UUID.randomUUID())
                .query("deck:Test")
                .limit(30)
                .mode(FilteredMode.INCLUDE_NEW)
                .returnPolicy(ReturnPolicy.ON_DELETE)
                .createdAt(now)
                .expiresAt(null)
                .lastBuiltAt(null)
                .build();

        // Then
        assertNotNull(deck);
        assertEquals("Builder Test", deck.name());
        assertEquals(30, deck.limit());
    }

    @Test
    void shouldSupportToBuilder() {
        // Given
        FilteredDeck original = FilteredDeck.create("Original", UUID.randomUUID(), "is:due", 20,
                FilteredMode.REVIEW_ONLY, ReturnPolicy.WHEN_EMPTY, null, Instant.now());

        // When
        FilteredDeck modified = original.toBuilder()
                .name("Modified")
                .limit(50)
                .build();

        // Then
        assertEquals(original.id(), modified.id());
        assertEquals("Modified", modified.name());
        assertEquals(50, modified.limit());
    }
}
