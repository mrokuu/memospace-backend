package org.project.memospace.domain.model;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NoteTest {

    @Test
    void shouldCreateNote() {
        // Given
        Long deckId = 1L;
        UUID noteTypeId = UUID.randomUUID();
        Map<String, String> fields = Map.of("Front", "Hello", "Back", "World");
        Set<String> tags = Set.of("vocab", "basic");

        // When
        Note note = Note.create(deckId, noteTypeId, fields, tags);

        // Then
        assertNotNull(note.id());
        assertEquals(deckId, note.deckId());
        assertEquals(noteTypeId, note.noteTypeId());
        assertEquals(2, note.fieldValues().size());
        assertEquals("Hello", note.fieldValues().get("Front"));
        assertEquals(2, note.tags().size());
        assertNotNull(note.createdAt());
        assertNotNull(note.updatedAt());
    }

    @Test
    void shouldNormalizeTagsToLowercase() {
        // Given
        Set<String> tags = Set.of("VOCAB", "Basic");

        // When
        Note note = Note.create(1L, UUID.randomUUID(), Map.of("Front", "Test"), tags);

        // Then
        assertTrue(note.tags().contains("vocab"));
        assertTrue(note.tags().contains("basic"));
        assertFalse(note.tags().contains("VOCAB"));
    }

    @Test
    void shouldRejectNullDeckId() {
        // When / Then
        assertThrows(NullPointerException.class, () ->
                Note.create(null, UUID.randomUUID(), Map.of("Front", "Test"), Set.of())
        );
    }

    @Test
    void shouldRejectNullNoteTypeId() {
        // When / Then
        assertThrows(NullPointerException.class, () ->
                Note.create(1L, null, Map.of("Front", "Test"), Set.of())
        );
    }

    @Test
    void shouldRejectNullFieldValues() {
        // When / Then
        assertThrows(NullPointerException.class, () ->
                Note.create(1L, UUID.randomUUID(), null, Set.of())
        );
    }

    @Test
    void shouldRejectNullTags() {
        // When / Then
        assertThrows(NullPointerException.class, () ->
                Note.create(1L, UUID.randomUUID(), Map.of("Front", "Test"), null)
        );
    }

    @Test
    void shouldRejectOversizedFieldValue() {
        // Given
        String tooLong = "a".repeat(16001);

        // When / Then
        assertThrows(IllegalArgumentException.class, () ->
                Note.create(1L, UUID.randomUUID(), Map.of("Front", tooLong), Set.of())
        );
    }

    @Test
    void shouldRejectOversizedTag() {
        // Given
        String tooLong = "a".repeat(65);

        // When / Then
        assertThrows(IllegalArgumentException.class, () ->
                Note.create(1L, UUID.randomUUID(), Map.of("Front", "Test"), Set.of(tooLong))
        );
    }

    @Test
    void shouldUpdateNote() {
        // Given
        Note original = Note.create(
                1L,
                UUID.randomUUID(),
                Map.of("Front", "Old"),
                Set.of("old")
        );

        // When
        Note updated = original.update(
                2L,
                Map.of("Front", "New"),
                Set.of("new")
        );

        // Then
        assertEquals(2L, updated.deckId());
        assertEquals("New", updated.fieldValues().get("Front"));
        assertTrue(updated.tags().contains("new"));
        assertEquals(original.id(), updated.id()); // ID preserved
        assertTrue(updated.updatedAt().isAfter(original.updatedAt()) ||
                   updated.updatedAt().isEqual(original.updatedAt()));
    }

    @Test
    void shouldGetFieldValue() {
        // Given
        Note note = Note.create(
                1L,
                UUID.randomUUID(),
                Map.of("Front", "Hello", "Back", "World"),
                Set.of()
        );

        // When / Then
        assertEquals("Hello", note.getFieldValue("Front"));
        assertEquals("World", note.getFieldValue("Back"));
        assertEquals("", note.getFieldValue("NonExistent")); // Returns empty for missing
    }
}
