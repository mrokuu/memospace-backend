package org.project.memospace.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void shouldCreateNewCardWithDefaultValues() {
        Card card = Card.create(1L, "Question", "Answer", List.of("tag1"));

        assertNull(card.id());
        assertEquals(1L, card.deckId());
        assertEquals("Question", card.front());
        assertEquals("Answer", card.back());
        assertEquals(List.of("tag1"), card.tags());
        assertEquals(2.5, card.easeFactor());
        assertEquals(1, card.intervalDays());
        assertEquals(0, card.repetitions());
        assertNotNull(card.dueAt());
        assertNotNull(card.createdAt());
        assertNotNull(card.updatedAt());
    }

    @Test
    void shouldUpdateContentAndTimestamp() {
        Card original = Card.create(1L, "Old question", "Old answer", List.of("old"));
        LocalDateTime originalUpdatedAt = original.updatedAt();

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Card updated = original.updateContent("New question", "New answer", List.of("new"));

        assertEquals("New question", updated.front());
        assertEquals("New answer", updated.back());
        assertEquals(List.of("new"), updated.tags());
        assertTrue(updated.updatedAt().isAfter(originalUpdatedAt));
        assertEquals(original.createdAt(), updated.createdAt()); // Should not change
    }

    @Test
    void shouldUpdateSchedulingValues() {
        Card original = Card.create(1L, "Question", "Answer", List.of());
        LocalDateTime newDueAt = LocalDateTime.now().plusDays(7);

        Card updated = original.updateScheduling(3.0, 7, 2, newDueAt);

        assertEquals(3.0, updated.easeFactor());
        assertEquals(7, updated.intervalDays());
        assertEquals(2, updated.repetitions());
        assertEquals(newDueAt, updated.dueAt());
    }

    @Test
    void shouldBeIsDueWhenDueAtIsInPast() {
        LocalDateTime pastTime = LocalDateTime.now().minusHours(1);
        Card card = Card.create(1L, "Question", "Answer", List.of())
                .updateScheduling(2.5, 1, 0, pastTime);

        assertTrue(card.isDue());
    }

    @Test
    void shouldBeIsDueWhenDueAtIsNow() {
        LocalDateTime now = LocalDateTime.now();
        Card card = Card.create(1L, "Question", "Answer", List.of())
                .updateScheduling(2.5, 1, 0, now);

        assertTrue(card.isDue());
    }

    @Test
    void shouldNotBeIsDueWhenDueAtIsInFuture() {
        LocalDateTime futureTime = LocalDateTime.now().plusHours(1);
        Card card = Card.create(1L, "Question", "Answer", List.of())
                .updateScheduling(2.5, 1, 0, futureTime);

        assertFalse(card.isDue());
    }

    @Test
    void shouldRequireNonNullValues() {
        assertThrows(NullPointerException.class, () ->
                Card.create(null, "Question", "Answer", List.of()));
        assertThrows(NullPointerException.class, () ->
                Card.create(1L, null, "Answer", List.of()));
        assertThrows(NullPointerException.class, () ->
                Card.create(1L, "Question", null, List.of()));
        assertThrows(NullPointerException.class, () ->
                Card.create(1L, "Question", "Answer", null));
    }
}