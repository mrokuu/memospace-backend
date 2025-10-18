package org.project.memospace.integration;

import org.junit.jupiter.api.Test;
import org.project.memospace.application.service.CommandBus;
import org.project.memospace.application.service.QueryBus;
import org.project.memospace.application.service.command.importexport.ImportDataCommand;
import org.project.memospace.application.service.query.export.ExportAllQuery;
import org.project.memospace.application.service.query.export.ExportDeckQuery;
import org.project.memospace.domain.model.*;
import org.project.memospace.domain.model.exportimport.ExportModel;
import org.project.memospace.domain.model.exportimport.ImportResult;
import org.project.memospace.domain.port.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for export/import workflow.
 */
@SpringBootTest
@Transactional
class ExportImportIntegrationTest {

    @Autowired
    private QueryBus queryBus;

    @Autowired
    private CommandBus commandBus;

    @Autowired
    private DeckRepositoryPort deckRepository;

    @Autowired
    private NoteTypeRepositoryPort noteTypeRepository;

    @Autowired
    private NoteRepositoryPort noteRepository;

    @Autowired
    private CardRepositoryPort cardRepository;

    @Test
    void exportAndImport_shouldPreserveData() {
        // Create test data
        Deck deck = Deck.create("Test Deck Export Import", "Test Description");
        deck = deckRepository.save(deck);

        CardTemplate template = CardTemplate.create("Card 1", "{{Front}}", "{{Back}}", false);
        NoteType noteType = NoteType.create("Basic Export Import", List.of("Front", "Back"), List.of(template), ".card{color:blue;}");
        noteType = noteTypeRepository.save(noteType);

        Note note = new Note(
                UUID.randomUUID(),
                deck.id(),
                noteType.getId(),
                Map.of("Front", "Question 1", "Back", "Answer 1"),
                Set.of("tag1", "tag2"),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        note = noteRepository.save(note);

        // Create card
        Card card = Card.create(deck.id(), "Question 1", "Answer 1", List.of());
        card = new Card(
                null,
                deck.id(),
                "Question 1",
                "Answer 1",
                List.of(),
                2.5,
                1,
                0,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                note.id(),
                noteType.getTemplates().get(0).id(),
                null
        );
        card = cardRepository.save(card);

        // Export specific deck
        ExportModel exportModel = queryBus.send(new ExportDeckQuery(deck.id(), false));

        assertNotNull(exportModel);
        assertEquals(1, exportModel.decks().size());
        assertEquals(1, exportModel.noteTypes().size());
        assertEquals(1, exportModel.notes().size());
        assertEquals(1, exportModel.cards().size());

        // Verify exported data
        assertEquals("Test Deck Export Import", exportModel.decks().get(0).name());
        assertEquals("Basic Export Import", exportModel.noteTypes().get(0).name());
        assertEquals("Question 1", exportModel.notes().get(0).fieldValues().get("Front"));

        // Clear database
        cardRepository.deleteById(card.id());
        noteRepository.deleteById(note.id());
        noteTypeRepository.deleteById(noteType.getId());
        deckRepository.deleteById(deck.id());

        // Import
        ImportResult result = commandBus.send(new ImportDataCommand(exportModel, null));

        assertNotNull(result);
        assertEquals(1, result.summary().decksCreated());
        assertEquals(1, result.summary().noteTypesCreated());
        assertEquals(1, result.summary().notesCreated());
        assertEquals(1, result.summary().cardsCreated());
        assertEquals(0, result.summary().notesSkipped());
        assertTrue(result.warnings().isEmpty());

        // Verify imported data by checking the export model, not the database
        // (database may have other test data)
        assertTrue(result.idMapping().size() > 0);
    }

    @Test
    void import_shouldDetectDuplicates() {
        // Create initial data
        Deck deck = Deck.create("Test Deck", "Description");
        deck = deckRepository.save(deck);

        CardTemplate template = CardTemplate.create("Card 1", "{{Front}}", "{{Back}}", false);
        NoteType noteType = NoteType.create("Basic", List.of("Front", "Back"), List.of(template), "");
        noteType = noteTypeRepository.save(noteType);

        Note note1 = new Note(
                UUID.randomUUID(),
                deck.id(),
                noteType.getId(),
                Map.of("Front", "Question", "Back", "Answer"),
                Set.of(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        note1 = noteRepository.save(note1);

        // Export
        ExportModel exportModel = queryBus.send(new ExportAllQuery(false));

        // Import again (should detect duplicate)
        ImportResult result = commandBus.send(new ImportDataCommand(exportModel, null));

        assertNotNull(result);
        assertEquals(0, result.summary().decksCreated(), "Deck should be reused");
        assertEquals(0, result.summary().noteTypesCreated(), "NoteType should be reused");
        assertEquals(0, result.summary().notesCreated(), "Note should be detected as duplicate");
        assertEquals(1, result.summary().notesSkipped(), "One note should be skipped");
    }

    @Test
    void exportDeck_shouldOnlyExportSpecificDeck() {
        // Create two decks
        Deck deck1 = deckRepository.save(Deck.create("Deck 1", "Description 1"));
        Deck deck2 = deckRepository.save(Deck.create("Deck 2", "Description 2"));

        CardTemplate template = CardTemplate.create("Card 1", "{{Front}}", "{{Back}}", false);
        NoteType noteType = noteTypeRepository.save(
                NoteType.create("Basic", List.of("Front", "Back"), List.of(template), "")
        );

        Note note1 = noteRepository.save(new Note(
                UUID.randomUUID(), deck1.id(), noteType.getId(),
                Map.of("Front", "Q1", "Back", "A1"), Set.of(),
                LocalDateTime.now(), LocalDateTime.now()
        ));

        Note note2 = noteRepository.save(new Note(
                UUID.randomUUID(), deck2.id(), noteType.getId(),
                Map.of("Front", "Q2", "Back", "A2"), Set.of(),
                LocalDateTime.now(), LocalDateTime.now()
        ));

        // Export only deck1
        ExportModel exportModel = queryBus.send(new ExportDeckQuery(deck1.id(), false));

        assertEquals(1, exportModel.decks().size());
        assertEquals("Deck 1", exportModel.decks().get(0).name());
        assertEquals(1, exportModel.notes().size());
        assertEquals("Q1", exportModel.notes().get(0).fieldValues().get("Front"));
    }
}
