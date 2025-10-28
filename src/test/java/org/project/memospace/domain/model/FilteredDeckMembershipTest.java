package org.project.memospace.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FilteredDeckMembershipTest {

    @Test
    void shouldCreateFilteredDeckMembershipWithAllFields() {
        // Given
        UUID filteredDeckId = UUID.randomUUID();
        Long cardId = 100L;
        int position = 5;
        Instant addedAt = Instant.now();

        // When
        FilteredDeckMembership membership = new FilteredDeckMembership(filteredDeckId, cardId, position, addedAt);

        // Then
        assertEquals(filteredDeckId, membership.filteredDeckId());
        assertEquals(cardId, membership.cardId());
        assertEquals(position, membership.position());
        assertEquals(addedAt, membership.addedAt());
    }

    @Test
    void shouldThrowExceptionWhenFilteredDeckIdIsNull() {
        // Given
        Instant now = Instant.now();

        // When/Then
        assertThrows(NullPointerException.class, () ->
                new FilteredDeckMembership(null, 100L, 0, now)
        );
    }

    @Test
    void shouldThrowExceptionWhenCardIdIsNull() {
        // Given
        UUID filteredDeckId = UUID.randomUUID();
        Instant now = Instant.now();

        // When/Then
        assertThrows(NullPointerException.class, () ->
                new FilteredDeckMembership(filteredDeckId, null, 0, now)
        );
    }

    @Test
    void shouldThrowExceptionWhenAddedAtIsNull() {
        // Given
        UUID filteredDeckId = UUID.randomUUID();

        // When/Then
        assertThrows(NullPointerException.class, () ->
                new FilteredDeckMembership(filteredDeckId, 100L, 0, null)
        );
    }

    @Test
    void shouldCreateMembershipUsingFactoryMethod() {
        // Given
        UUID filteredDeckId = UUID.randomUUID();
        Long cardId = 200L;
        int position = 10;
        Instant now = Instant.now();

        // When
        FilteredDeckMembership membership = FilteredDeckMembership.create(filteredDeckId, cardId, position, now);

        // Then
        assertEquals(filteredDeckId, membership.filteredDeckId());
        assertEquals(cardId, membership.cardId());
        assertEquals(position, membership.position());
        assertEquals(now, membership.addedAt());
    }

    @Test
    void shouldHandleZeroPosition() {
        // Given
        UUID filteredDeckId = UUID.randomUUID();
        Instant now = Instant.now();

        // When
        FilteredDeckMembership membership = FilteredDeckMembership.create(filteredDeckId, 100L, 0, now);

        // Then
        assertEquals(0, membership.position());
    }

    @Test
    void shouldHandleHighPosition() {
        // Given
        UUID filteredDeckId = UUID.randomUUID();
        Instant now = Instant.now();

        // When
        FilteredDeckMembership membership = FilteredDeckMembership.create(filteredDeckId, 100L, 999999, now);

        // Then
        assertEquals(999999, membership.position());
    }

    @Test
    void shouldSupportBuilderPattern() {
        // Given
        UUID filteredDeckId = UUID.randomUUID();
        Instant now = Instant.now();

        // When
        FilteredDeckMembership membership = FilteredDeckMembership.builder()
                .filteredDeckId(filteredDeckId)
                .cardId(100L)
                .position(5)
                .addedAt(now)
                .build();

        // Then
        assertEquals(filteredDeckId, membership.filteredDeckId());
        assertEquals(100L, membership.cardId());
        assertEquals(5, membership.position());
        assertEquals(now, membership.addedAt());
    }

    @Test
    void shouldSupportToBuilder() {
        // Given
        FilteredDeckMembership original = FilteredDeckMembership.create(
                UUID.randomUUID(), 100L, 5, Instant.now());

        // When
        FilteredDeckMembership modified = original.toBuilder()
                .position(10)
                .build();

        // Then
        assertEquals(original.filteredDeckId(), modified.filteredDeckId());
        assertEquals(original.cardId(), modified.cardId());
        assertEquals(10, modified.position());
        assertEquals(original.addedAt(), modified.addedAt());
    }
}
