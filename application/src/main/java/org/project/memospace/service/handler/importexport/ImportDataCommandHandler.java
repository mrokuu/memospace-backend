package org.project.memospace.application.service.handler.importexport;

import lombok.RequiredArgsConstructor;
import org.project.memospace.application.service.CommandHandler;
import org.project.memospace.application.service.command.importexport.ImportDataCommand;
import org.project.memospace.domain.model.*;
import org.project.memospace.domain.model.exportimport.*;
import org.project.memospace.domain.port.*;
import org.project.memospace.domain.service.DuplicateDetector;
import org.project.memospace.domain.service.ImportService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@RequiredArgsConstructor
public class ImportDataCommandHandler implements CommandHandler<ImportDataCommand, ImportResult> {

    private final DeckRepositoryPort deckRepository;
    private final NoteTypeRepositoryPort noteTypeRepository;
    private final NoteRepositoryPort noteRepository;
    private final CardRepositoryPort cardRepository;

    @Override
    @Transactional
    public ImportResult handle(ImportDataCommand command) {
        // Track actual deck IDs (temporary ID -> actual ID mapping)
        Map<String, Long> actualDeckIds = new HashMap<>();

        DuplicateDetector duplicateDetector = new DuplicateDetector();
        ImportService importService = new ImportService(
                deckRepository,
                noteTypeRepository,
                noteRepository,
                duplicateDetector
        );

        // Build import plan
        ImportPlan plan = importService.buildImportPlan(
                command.exportModel(),
                command.noteTypeMapping()
        );

        // Execute import plan
        int decksCreated = createDecks(plan, actualDeckIds);
        int noteTypesCreated = createNoteTypes(plan);
        int notesCreated = createNotes(plan, actualDeckIds);
        int cardsCreated = createCards(plan, actualDeckIds);

        // Build summary
        ImportSummary summary = new ImportSummary(
                decksCreated,
                noteTypesCreated,
                notesCreated,
                0, // notesUpdated - not implemented
                plan.notesToSkip().size(),
                cardsCreated,
                0, // cardsUpdated - not implemented
                0  // mediaLinked - not implemented in MVP
        );

        // Combine all ID mappings for response
        Map<String, String> combinedIdMapping = new HashMap<>();
        combinedIdMapping.putAll(plan.noteIdMapping());
        combinedIdMapping.putAll(plan.deckIdMapping());
        combinedIdMapping.putAll(plan.noteTypeIdMapping());

        return new ImportResult(summary, combinedIdMapping, plan.warnings());
    }

    private int createDecks(ImportPlan plan, Map<String, Long> actualDeckIds) {
        int count = 0;

        for (ExportDeck exportDeck : plan.decksToCreate()) {
            Deck deck = Deck.create(exportDeck.name(), exportDeck.description());
            Deck saved = deckRepository.save(deck);
            actualDeckIds.put(exportDeck.id(), saved.id());
            count++;
        }

        return count;
    }

    private int createNoteTypes(ImportPlan plan) {
        int count = 0;

        for (ExportNoteType exportNoteType : plan.noteTypesToCreate()) {
            List<CardTemplate> templates = new ArrayList<>();
            for (ExportCardTemplate exportTemplate : exportNoteType.templates()) {
                String newTemplateId = plan.templateIdMapping().get(exportTemplate.id());
                CardTemplate template = new CardTemplate(
                        UUID.fromString(newTemplateId),
                        exportTemplate.name(),
                        exportTemplate.frontTemplate(),
                        exportTemplate.backTemplate(),
                        exportTemplate.isCloze()
                );
                templates.add(template);
            }

            String newNoteTypeId = plan.noteTypeIdMapping().get(exportNoteType.id());
            NoteType noteType = new NoteType(
                    UUID.fromString(newNoteTypeId),
                    exportNoteType.name(),
                    exportNoteType.fields(),
                    templates,
                    exportNoteType.css(),
                    exportNoteType.createdAt(),
                    exportNoteType.updatedAt()
            );

            noteTypeRepository.save(noteType);
            count++;
        }

        return count;
    }

    private int createNotes(ImportPlan plan, Map<String, Long> actualDeckIds) {
        int count = 0;

        for (ExportNote exportNote : plan.notesToCreate()) {
            String newNoteId = plan.noteIdMapping().get(exportNote.id());
            String newDeckId = plan.deckIdMapping().get(exportNote.deckId());
            String newNoteTypeId = plan.noteTypeIdMapping().get(exportNote.noteTypeId());

            // Find actual deck ID (it might have been created)
            Long deckId = resolveDeckId(newDeckId, actualDeckIds);

            Note note = new Note(
                    UUID.fromString(newNoteId),
                    deckId,
                    UUID.fromString(newNoteTypeId),
                    exportNote.fieldValues(),
                    exportNote.tags(),
                    exportNote.createdAt(),
                    exportNote.updatedAt()
            );

            noteRepository.save(note);
            count++;
        }

        return count;
    }

    private int createCards(ImportPlan plan, Map<String, Long> actualDeckIds) {
        int count = 0;
        List<Card> cardsToSave = new ArrayList<>();

        for (ExportCard exportCard : plan.cardsToCreate()) {
            String newDeckId = plan.deckIdMapping().get(exportCard.deckId());
            String newNoteId = exportCard.noteId() != null ?
                    plan.noteIdMapping().get(exportCard.noteId()) : null;
            String newTemplateId = exportCard.templateId() != null ?
                    plan.templateIdMapping().get(exportCard.templateId()) : null;

            Long deckId = resolveDeckId(newDeckId, actualDeckIds);

            Card card = new Card(
                    null, // ID will be assigned
                    deckId,
                    exportCard.front(),
                    exportCard.back(),
                    List.of(), // tags - not in ExportCard
                    exportCard.easeFactor(),
                    exportCard.intervalDays(),
                    exportCard.repetitions(),
                    exportCard.dueAt(),
                    exportCard.createdAt(),
                    exportCard.updatedAt(),
                    newNoteId != null ? UUID.fromString(newNoteId) : null,
                    newTemplateId != null ? UUID.fromString(newTemplateId) : null,
                    null // clozeIndex - not in ExportCard
            );

            cardsToSave.add(card);
            count++;
        }

        // Batch save cards
        if (!cardsToSave.isEmpty()) {
            cardRepository.saveAll(cardsToSave);
        }

        return count;
    }

    private Long resolveDeckId(String deckIdString, Map<String, Long> actualDeckIds) {
        if (deckIdString.startsWith("temp_deck_")) {
            // This is a temporary ID - look it up in actualDeckIds
            Long actualId = actualDeckIds.values().stream().findFirst().orElse(null);
            if (actualId == null) {
                throw new IllegalStateException("Temporary deck IDs not yet resolved: " + deckIdString);
            }
            return actualId;
        }
        // Check if this is a mapped deck ID
        for (Map.Entry<String, Long> entry : actualDeckIds.entrySet()) {
            if (entry.getKey().equals(deckIdString)) {
                return entry.getValue();
            }
        }
        return Long.parseLong(deckIdString);
    }
}
