package org.project.memospace.domain.model.exportimport;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ImportPlanTest {

    @Test
    void shouldCreateImportPlanWithAllFields() {
        // Given
        List<ExportDeck> decksToCreate = List.of();
        List<ExportNoteType> noteTypesToCreate = List.of();
        List<ExportNote> notesToCreate = List.of();
        List<ExportNote> notesToUpdate = List.of();
        List<String> notesToSkip = List.of();
        List<ExportCard> cardsToCreate = List.of();
        Map<String, String> noteIdMapping = Map.of();
        Map<String, String> deckIdMapping = Map.of();
        Map<String, String> noteTypeIdMapping = Map.of();
        Map<String, String> templateIdMapping = Map.of();
        List<ImportWarning> warnings = List.of();

        // When
        ImportPlan plan = new ImportPlan(decksToCreate, noteTypesToCreate, notesToCreate, notesToUpdate,
                notesToSkip, cardsToCreate, noteIdMapping, deckIdMapping, noteTypeIdMapping,
                templateIdMapping, warnings);

        // Then
        assertNotNull(plan.decksToCreate());
        assertNotNull(plan.noteTypesToCreate());
        assertNotNull(plan.notesToCreate());
        assertNotNull(plan.notesToUpdate());
        assertNotNull(plan.notesToSkip());
        assertNotNull(plan.cardsToCreate());
        assertNotNull(plan.noteIdMapping());
        assertNotNull(plan.deckIdMapping());
        assertNotNull(plan.noteTypeIdMapping());
        assertNotNull(plan.templateIdMapping());
        assertNotNull(plan.warnings());
    }

    @Test
    void shouldCreateEmptyImportPlan() {
        // When
        ImportPlan plan = ImportPlan.empty();

        // Then
        assertTrue(plan.decksToCreate().isEmpty());
        assertTrue(plan.noteTypesToCreate().isEmpty());
        assertTrue(plan.notesToCreate().isEmpty());
        assertTrue(plan.notesToUpdate().isEmpty());
        assertTrue(plan.notesToSkip().isEmpty());
        assertTrue(plan.cardsToCreate().isEmpty());
        assertTrue(plan.noteIdMapping().isEmpty());
        assertTrue(plan.deckIdMapping().isEmpty());
        assertTrue(plan.noteTypeIdMapping().isEmpty());
        assertTrue(plan.templateIdMapping().isEmpty());
        assertTrue(plan.warnings().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenDecksToCreateIsNull() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () ->
                new ImportPlan(null, List.of(), List.of(), List.of(), List.of(), List.of(),
                        Map.of(), Map.of(), Map.of(), Map.of(), List.of())
        );
    }

    @Test
    void shouldThrowExceptionWhenNoteTypesToCreateIsNull() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () ->
                new ImportPlan(List.of(), null, List.of(), List.of(), List.of(), List.of(),
                        Map.of(), Map.of(), Map.of(), Map.of(), List.of())
        );
    }

    @Test
    void shouldThrowExceptionWhenNotesToCreateIsNull() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () ->
                new ImportPlan(List.of(), List.of(), null, List.of(), List.of(), List.of(),
                        Map.of(), Map.of(), Map.of(), Map.of(), List.of())
        );
    }

    @Test
    void shouldThrowExceptionWhenNotesToUpdateIsNull() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () ->
                new ImportPlan(List.of(), List.of(), List.of(), null, List.of(), List.of(),
                        Map.of(), Map.of(), Map.of(), Map.of(), List.of())
        );
    }

    @Test
    void shouldThrowExceptionWhenNotesToSkipIsNull() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () ->
                new ImportPlan(List.of(), List.of(), List.of(), List.of(), null, List.of(),
                        Map.of(), Map.of(), Map.of(), Map.of(), List.of())
        );
    }

    @Test
    void shouldThrowExceptionWhenCardsToCreateIsNull() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () ->
                new ImportPlan(List.of(), List.of(), List.of(), List.of(), List.of(), null,
                        Map.of(), Map.of(), Map.of(), Map.of(), List.of())
        );
    }

    @Test
    void shouldThrowExceptionWhenNoteIdMappingIsNull() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () ->
                new ImportPlan(List.of(), List.of(), List.of(), List.of(), List.of(), List.of(),
                        null, Map.of(), Map.of(), Map.of(), List.of())
        );
    }

    @Test
    void shouldThrowExceptionWhenDeckIdMappingIsNull() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () ->
                new ImportPlan(List.of(), List.of(), List.of(), List.of(), List.of(), List.of(),
                        Map.of(), null, Map.of(), Map.of(), List.of())
        );
    }

    @Test
    void shouldThrowExceptionWhenNoteTypeIdMappingIsNull() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () ->
                new ImportPlan(List.of(), List.of(), List.of(), List.of(), List.of(), List.of(),
                        Map.of(), Map.of(), null, Map.of(), List.of())
        );
    }

    @Test
    void shouldThrowExceptionWhenTemplateIdMappingIsNull() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () ->
                new ImportPlan(List.of(), List.of(), List.of(), List.of(), List.of(), List.of(),
                        Map.of(), Map.of(), Map.of(), null, List.of())
        );
    }

    @Test
    void shouldThrowExceptionWhenWarningsIsNull() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () ->
                new ImportPlan(List.of(), List.of(), List.of(), List.of(), List.of(), List.of(),
                        Map.of(), Map.of(), Map.of(), Map.of(), null)
        );
    }

    @Test
    void shouldCreateUnmodifiableLists() {
        // Given
        List<String> notesToSkip = new ArrayList<>(List.of("note1", "note2"));
        ImportPlan plan = new ImportPlan(List.of(), List.of(), List.of(), List.of(), notesToSkip,
                List.of(), Map.of(), Map.of(), Map.of(), Map.of(), List.of());

        // When/Then
        assertThrows(UnsupportedOperationException.class, () ->
                plan.notesToSkip().add("note3")
        );
    }

    @Test
    void shouldCreateUnmodifiableMaps() {
        // Given
        Map<String, String> noteIdMapping = new HashMap<>(Map.of("old1", "new1"));
        ImportPlan plan = new ImportPlan(List.of(), List.of(), List.of(), List.of(), List.of(),
                List.of(), noteIdMapping, Map.of(), Map.of(), Map.of(), List.of());

        // When/Then
        assertThrows(UnsupportedOperationException.class, () ->
                plan.noteIdMapping().put("old2", "new2")
        );
    }

    @Test
    void shouldDefensiveCopyLists() {
        // Given
        List<String> notesToSkip = new ArrayList<>(List.of("note1"));
        ImportPlan plan = new ImportPlan(List.of(), List.of(), List.of(), List.of(), notesToSkip,
                List.of(), Map.of(), Map.of(), Map.of(), Map.of(), List.of());

        // When
        notesToSkip.add("note2");

        // Then
        assertEquals(1, plan.notesToSkip().size());
    }

    @Test
    void shouldDefensiveCopyMaps() {
        // Given
        Map<String, String> noteIdMapping = new HashMap<>(Map.of("old1", "new1"));
        ImportPlan plan = new ImportPlan(List.of(), List.of(), List.of(), List.of(), List.of(),
                List.of(), noteIdMapping, Map.of(), Map.of(), Map.of(), List.of());

        // When
        noteIdMapping.put("old2", "new2");

        // Then
        assertEquals(1, plan.noteIdMapping().size());
    }

    @Test
    void shouldSupportBuilderPattern() {
        // When
        ImportPlan plan = ImportPlan.builder()
                .decksToCreate(List.of())
                .noteTypesToCreate(List.of())
                .notesToCreate(List.of())
                .notesToUpdate(List.of())
                .notesToSkip(List.of("note1"))
                .cardsToCreate(List.of())
                .noteIdMapping(Map.of("old", "new"))
                .deckIdMapping(Map.of())
                .noteTypeIdMapping(Map.of())
                .templateIdMapping(Map.of())
                .warnings(List.of())
                .build();

        // Then
        assertEquals(1, plan.notesToSkip().size());
        assertEquals(1, plan.noteIdMapping().size());
    }

    @Test
    void shouldSupportToBuilder() {
        // Given
        ImportPlan original = ImportPlan.empty();

        // When
        ImportPlan modified = original.toBuilder()
                .notesToSkip(List.of("skipped1"))
                .build();

        // Then
        assertTrue(original.notesToSkip().isEmpty());
        assertEquals(1, modified.notesToSkip().size());
    }
}
