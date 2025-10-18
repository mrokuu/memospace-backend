package org.project.memospace.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.memospace.domain.model.Note;
import org.project.memospace.domain.model.NoteType;
import org.project.memospace.domain.model.CardTemplate;
import org.project.memospace.domain.model.exportimport.ExportNote;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DuplicateDetectorTest {
    private DuplicateDetector duplicateDetector;

    @BeforeEach
    void setUp() {
        duplicateDetector = new DuplicateDetector();
    }

    @Test
    void computeNoteHash_shouldProduceSameHashForIdenticalContent() {
        Map<String, String> fields1 = Map.of("Front", "Question", "Back", "Answer");
        Map<String, String> fields2 = Map.of("Front", "Question", "Back", "Answer");

        String hash1 = duplicateDetector.computeNoteHash("Basic", fields1);
        String hash2 = duplicateDetector.computeNoteHash("Basic", fields2);

        assertEquals(hash1, hash2);
    }

    @Test
    void computeNoteHash_shouldProduceDifferentHashForDifferentContent() {
        Map<String, String> fields1 = Map.of("Front", "Question 1", "Back", "Answer 1");
        Map<String, String> fields2 = Map.of("Front", "Question 2", "Back", "Answer 2");

        String hash1 = duplicateDetector.computeNoteHash("Basic", fields1);
        String hash2 = duplicateDetector.computeNoteHash("Basic", fields2);

        assertNotEquals(hash1, hash2);
    }

    @Test
    void computeNoteHash_shouldNormalizeWhitespace() {
        Map<String, String> fields1 = Map.of("Front", "Question   with   spaces", "Back", "Answer");
        Map<String, String> fields2 = Map.of("Front", "Question with spaces", "Back", "Answer");

        String hash1 = duplicateDetector.computeNoteHash("Basic", fields1);
        String hash2 = duplicateDetector.computeNoteHash("Basic", fields2);

        assertEquals(hash1, hash2, "Hashes should be equal after whitespace normalization");
    }

    @Test
    void computeNoteHash_shouldNormalizeCase() {
        Map<String, String> fields1 = Map.of("Front", "QUESTION", "Back", "ANSWER");
        Map<String, String> fields2 = Map.of("Front", "question", "Back", "answer");

        String hash1 = duplicateDetector.computeNoteHash("Basic", fields1);
        String hash2 = duplicateDetector.computeNoteHash("Basic", fields2);

        assertEquals(hash1, hash2, "Hashes should be equal after case normalization");
    }

    @Test
    void computeNoteHash_shouldBeOrderIndependentForFields() {
        // Field order shouldn't matter
        Map<String, String> fields1 = new LinkedHashMap<>();
        fields1.put("Front", "Question");
        fields1.put("Back", "Answer");
        fields1.put("Extra", "Info");

        Map<String, String> fields2 = new LinkedHashMap<>();
        fields2.put("Extra", "Info");
        fields2.put("Front", "Question");
        fields2.put("Back", "Answer");

        String hash1 = duplicateDetector.computeNoteHash("Basic", fields1);
        String hash2 = duplicateDetector.computeNoteHash("Basic", fields2);

        assertEquals(hash1, hash2, "Field order should not affect hash");
    }

    @Test
    void computeNoteHash_shouldDifferForDifferentNoteType() {
        Map<String, String> fields = Map.of("Front", "Question", "Back", "Answer");

        String hash1 = duplicateDetector.computeNoteHash("Basic", fields);
        String hash2 = duplicateDetector.computeNoteHash("Cloze", fields);

        assertNotEquals(hash1, hash2, "Different note types should produce different hashes");
    }

    @Test
    void computeNoteHash_withDomainNote_shouldWork() {
        UUID noteTypeId = UUID.randomUUID();
        CardTemplate template = CardTemplate.create("Card 1", "{{Front}}", "{{Back}}", false);
        NoteType noteType = NoteType.create("Basic", List.of("Front", "Back"), List.of(template), "");

        LocalDateTime now = LocalDateTime.now();
        Note note = new Note(
                UUID.randomUUID(),
                1L,
                noteTypeId,
                Map.of("Front", "Question", "Back", "Answer"),
                Set.of(),
                now,
                now
        );

        String hash = duplicateDetector.computeNoteHash(note, noteType);

        assertNotNull(hash);
        assertEquals(64, hash.length(), "SHA-256 hex should be 64 characters");
    }

    @Test
    void computeNoteHash_withExportNote_shouldWork() {
        ExportNote exportNote = new ExportNote(
                UUID.randomUUID().toString(),
                "1",
                UUID.randomUUID().toString(),
                Map.of("Front", "Question", "Back", "Answer"),
                Set.of(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        String hash = duplicateDetector.computeNoteHash(exportNote, "Basic");

        assertNotNull(hash);
        assertEquals(64, hash.length(), "SHA-256 hex should be 64 characters");
    }

    @Test
    void computeNoteHash_shouldHandleEmptyFields() {
        Map<String, String> fields = Map.of("Front", "", "Back", "");

        String hash = duplicateDetector.computeNoteHash("Basic", fields);

        assertNotNull(hash);
        assertEquals(64, hash.length());
    }

    @Test
    void computeNoteHash_shouldThrowForNullNoteTypeName() {
        Map<String, String> fields = Map.of("Front", "Question");

        assertThrows(IllegalArgumentException.class, () ->
                duplicateDetector.computeNoteHash(null, fields)
        );
    }

    @Test
    void computeNoteHash_shouldThrowForNullFieldValues() {
        assertThrows(IllegalArgumentException.class, () ->
                duplicateDetector.computeNoteHash("Basic", null)
        );
    }
}
