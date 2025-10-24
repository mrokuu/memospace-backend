package org.project.memospace.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.memospace.domain.model.*;
import org.project.memospace.domain.model.exportimport.*;
import org.project.memospace.domain.port.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImportServiceTest {

    @Mock
    private DeckRepositoryPort deckRepository;
    @Mock
    private NoteTypeRepositoryPort noteTypeRepository;
    @Mock
    private NoteRepositoryPort noteRepository;
    @Mock
    private DuplicateDetector duplicateDetector;

    private ImportService importService;

    @BeforeEach
    void setUp() {
        importService = new ImportService(
                deckRepository,
                noteTypeRepository,
                noteRepository,
                duplicateDetector
        );
    }

    @Test
    void shouldWarnOnUnsupportedVersion() {
        // Given
        ExportModel exportModel = new ExportModel(
                99, // Unsupported version
                Instant.now(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );

        // When
        ImportPlan plan = importService.buildImportPlan(exportModel, null);

        // Then
        assertEquals(1, plan.warnings().size());
        assertEquals(ImportConflict.UNSUPPORTED_VERSION, plan.warnings().get(0).code());
        assertTrue(plan.warnings().get(0).detail().contains("99"));
    }

    @Test
    void shouldCreateMappingsForEmptyImport() {
        // Given
        ExportModel exportModel = createExportModel(List.of(), List.of(), List.of(), List.of());

        // When
        ImportPlan plan = importService.buildImportPlan(exportModel, null);

        // Then
        assertNotNull(plan);
        assertTrue(plan.decksToCreate().isEmpty());
        assertTrue(plan.noteTypesToCreate().isEmpty());
        assertTrue(plan.notesToCreate().isEmpty());
        assertTrue(plan.warnings().isEmpty() ||
                   plan.warnings().stream().allMatch(w -> w.code() != ImportConflict.UNSUPPORTED_VERSION));
    }

    @Test
    void shouldPlanDeckCreationWhenDeckDoesNotExist() {
        // Given
        when(deckRepository.findAll()).thenReturn(List.of());

        ExportDeck exportDeck = new ExportDeck("deck-1", "Spanish", "Vocab", LocalDateTime.now());
        ExportModel exportModel = createExportModel(List.of(exportDeck), List.of(), List.of(), List.of());

        // When
        ImportPlan plan = importService.buildImportPlan(exportModel, null);

        // Then
        assertEquals(1, plan.decksToCreate().size());
        assertEquals("Spanish", plan.decksToCreate().get(0).name());
        assertTrue(plan.deckIdMapping().containsKey("deck-1"));
    }

    @Test
    void shouldReuseDeckWhenDeckExists() {
        // Given
        Deck existingDeck = Deck.create("Spanish", "Existing").withId(1L);
        when(deckRepository.findAll()).thenReturn(List.of(existingDeck));

        ExportDeck exportDeck = new ExportDeck("deck-1", "Spanish", "Vocab", LocalDateTime.now());
        ExportModel exportModel = createExportModel(List.of(exportDeck), List.of(), List.of(), List.of());

        // When
        ImportPlan plan = importService.buildImportPlan(exportModel, null);

        // Then
        assertEquals(0, plan.decksToCreate().size());
        assertEquals("1", plan.deckIdMapping().get("deck-1"));
    }

    @Test
    void shouldPlanNoteTypeCreationWhenNoteTypeDoesNotExist() {
        // Given
        when(noteTypeRepository.findAll()).thenReturn(List.of());

        ExportNoteType exportNoteType = createExportNoteType("nt-1", "Basic");
        ExportModel exportModel = createExportModel(List.of(), List.of(exportNoteType), List.of(), List.of());

        // When
        ImportPlan plan = importService.buildImportPlan(exportModel, null);

        // Then
        assertEquals(1, plan.noteTypesToCreate().size());
        assertEquals("Basic", plan.noteTypesToCreate().get(0).name());
        assertTrue(plan.noteTypeIdMapping().containsKey("nt-1"));
    }

    @Test
    void shouldReuseNoteTypeWhenNoteTypeExists() {
        // Given
        NoteType existingNoteType = NoteType.create(
                "Basic",
                Arrays.asList("Front", "Back"),
                List.of(CardTemplate.create("Card", "{{Front}}", "{{Back}}", false)),
                null
        );
        when(noteTypeRepository.findAll()).thenReturn(List.of(existingNoteType));

        ExportNoteType exportNoteType = createExportNoteType("nt-1", "Basic");
        ExportModel exportModel = createExportModel(List.of(), List.of(exportNoteType), List.of(), List.of());

        // When
        ImportPlan plan = importService.buildImportPlan(exportModel, null);

        // Then
        assertEquals(0, plan.noteTypesToCreate().size());
        assertEquals(existingNoteType.getId().toString(), plan.noteTypeIdMapping().get("nt-1"));
    }

    @Test
    void shouldWarnOnNoteTypeShapeMismatch() {
        // Given
        NoteType existingNoteType = NoteType.create(
                "Basic",
                Arrays.asList("Front", "Back", "Extra"), // Different fields
                List.of(CardTemplate.create("Card", "{{Front}}", "{{Back}}", false)),
                null
        );
        when(noteTypeRepository.findAll()).thenReturn(List.of(existingNoteType));

        ExportNoteType exportNoteType = createExportNoteType("nt-1", "Basic");
        ExportModel exportModel = createExportModel(List.of(), List.of(exportNoteType), List.of(), List.of());

        // When
        ImportPlan plan = importService.buildImportPlan(exportModel, null);

        // Then
        assertTrue(plan.warnings().stream()
                .anyMatch(w -> w.code() == ImportConflict.NOTETYPE_SHAPE_MISMATCH &&
                               w.detail().contains("Basic")));
    }

    private ExportModel createExportModel(List<ExportDeck> decks, List<ExportNoteType> noteTypes,
                                           List<ExportNote> notes, List<ExportCard> cards) {
        return new ExportModel(1, Instant.now(), decks, noteTypes, notes, cards, List.of());
    }

    private ExportNoteType createExportNoteType(String id, String name) {
        ExportCardTemplate template = new ExportCardTemplate(
                "template-1",
                "Card",
                "{{Front}}",
                "{{Back}}",
                false
        );
        return new ExportNoteType(
                id,
                name,
                Arrays.asList("Front", "Back"),
                List.of(template),
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
