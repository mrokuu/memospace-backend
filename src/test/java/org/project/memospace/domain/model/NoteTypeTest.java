package org.project.memospace.domain.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NoteTypeTest {

    @Test
    void shouldCreateBasicNoteType() {
        // Given
        List<String> fields = Arrays.asList("Front", "Back");
        List<CardTemplate> templates = List.of(
                CardTemplate.create("Card 1", "{{Front}}", "{{Back}}", false)
        );

        // When
        NoteType noteType = NoteType.create("Basic", fields, templates, null);

        // Then
        assertNotNull(noteType.getId());
        assertEquals("Basic", noteType.getName());
        assertEquals(2, noteType.getFields().size());
        assertEquals(1, noteType.getTemplates().size());
        assertNull(noteType.getCss());
        assertNotNull(noteType.getCreatedAt());
        assertNotNull(noteType.getUpdatedAt());
    }

    @Test
    void shouldCreateNoteTypeWithCSS() {
        // Given
        String css = ".card { font-size: 20px; }";

        // When
        NoteType noteType = NoteType.create(
                "Custom",
                List.of("Front"),
                List.of(CardTemplate.create("Card", "{{Front}}", "{{Front}}", false)),
                css
        );

        // Then
        assertEquals(css, noteType.getCss());
    }

    @Test
    void shouldRejectEmptyFields() {
        // When / Then
        assertThrows(IllegalArgumentException.class, () ->
                NoteType.create(
                        "Test",
                        List.of(),
                        List.of(CardTemplate.create("Card", "{{Front}}", "{{Back}}", false)),
                        null
                )
        );
    }

    @Test
    void shouldRejectEmptyTemplates() {
        // When / Then
        assertThrows(IllegalArgumentException.class, () ->
                NoteType.create("Test", List.of("Front"), List.of(), null)
        );
    }

    @Test
    void shouldRejectDuplicateFields() {
        // When / Then
        assertThrows(IllegalArgumentException.class, () ->
                NoteType.create(
                        "Test",
                        Arrays.asList("Front", "FRONT"), // Duplicate (case-insensitive)
                        List.of(CardTemplate.create("Card", "{{Front}}", "{{Front}}", false)),
                        null
                )
        );
    }

    @Test
    void shouldRejectTemplateWithUnknownField() {
        // When / Then
        assertThrows(IllegalArgumentException.class, () ->
                NoteType.create(
                        "Test",
                        List.of("Front"),
                        List.of(CardTemplate.create("Card", "{{Unknown}}", "{{Front}}", false)),
                        null
                )
        );
    }

    @Test
    void shouldAcceptClozeFieldReference() {
        // Given
        List<String> fields = List.of("Text");
        List<CardTemplate> templates = List.of(
                CardTemplate.create("Cloze", "{{cloze:Text}}", "{{cloze:Text}}", true)
        );

        // When / Then
        assertDoesNotThrow(() ->
                NoteType.create("Cloze", fields, templates, null)
        );
    }

    @Test
    void shouldUpdateNoteType() {
        // Given
        NoteType original = NoteType.create(
                "Basic",
                List.of("Front", "Back"),
                List.of(CardTemplate.create("Card", "{{Front}}", "{{Back}}", false)),
                null
        );

        // When
        NoteType updated = original.update(
                "Basic Updated",
                List.of("Question", "Answer"),
                List.of(CardTemplate.create("Card", "{{Question}}", "{{Answer}}", false)),
                ".card { color: blue; }"
        );

        // Then
        assertEquals("Basic Updated", updated.getName());
        assertTrue(updated.getFields().contains("Question"));
        assertTrue(updated.getFields().contains("Answer"));
        assertEquals(".card { color: blue; }", updated.getCss());
        assertEquals(original.getId(), updated.getId()); // ID preserved
        assertEquals(original.getCreatedAt(), updated.getCreatedAt()); // Created preserved
        assertTrue(updated.getUpdatedAt().isAfter(original.getUpdatedAt()) ||
                   updated.getUpdatedAt().isEqual(original.getUpdatedAt()));
    }
}
