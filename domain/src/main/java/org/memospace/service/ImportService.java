package org.memospace.service;

import org.memospace.model.*;
import org.memospace.model.exportimport.*;
import org.memospace.port.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Domain service for importing flashcard collection data.
 * Pure domain logic with validation and merge decisions.
 */
public class ImportService {
    private final DeckRepositoryPort deckRepository;
    private final NoteTypeRepositoryPort noteTypeRepository;
    private final NoteRepositoryPort noteRepository;
    private final DuplicateDetector duplicateDetector;

    public ImportService(DeckRepositoryPort deckRepository, NoteTypeRepositoryPort noteTypeRepository,
                         NoteRepositoryPort noteRepository, DuplicateDetector duplicateDetector) {
        this.deckRepository = deckRepository;
        this.noteTypeRepository = noteTypeRepository;
        this.noteRepository = noteRepository;
        this.duplicateDetector = duplicateDetector;
    }

    /**
     * Builds an import plan from an export model.
     *
     * @param exportModel the export model to import
     * @param noteTypeMapping optional note type mapping (name -> existing UUID string)
     * @return import plan with decisions
     */
    public ImportPlan buildImportPlan(ExportModel exportModel, Map<String, String> noteTypeMapping) {
        List<ImportWarning> warnings = new ArrayList<>();

        // Validate version
        if (exportModel.version() != 1) {
            warnings.add(new ImportWarning(ImportConflict.UNSUPPORTED_VERSION,
                    "Unsupported export version: " + exportModel.version()));
        }

        // Build deck mapping
        Map<String, String> deckIdMapping = new HashMap<>();
        List<ExportDeck> decksToCreate = new ArrayList<>();
        for (ExportDeck exportDeck : exportModel.decks()) {
            Deck existingDeck = findDeckByName(exportDeck.name());
            if (existingDeck != null) {
                deckIdMapping.put(exportDeck.id(), String.valueOf(existingDeck.id()));
            } else {
                String newDeckId = generateDeckId(); // Will be assigned on save
                deckIdMapping.put(exportDeck.id(), newDeckId);
                decksToCreate.add(exportDeck);
            }
        }

        // Build note type mapping
        Map<String, String> noteTypeIdMapping = new HashMap<>();
        Map<String, String> templateIdMapping = new HashMap<>();
        List<ExportNoteType> noteTypesToCreate = new ArrayList<>();

        for (ExportNoteType exportNoteType : exportModel.noteTypes()) {
            String mappedNoteTypeName = noteTypeMapping != null ?
                    noteTypeMapping.get(exportNoteType.name()) : null;

            NoteType existingNoteType = null;
            if (mappedNoteTypeName != null) {
                existingNoteType = findNoteTypeByName(mappedNoteTypeName);
            } else {
                existingNoteType = findNoteTypeByName(exportNoteType.name());
            }

            if (existingNoteType != null) {
                // Validate shape matches
                if (!noteTypeShapeMatches(exportNoteType, existingNoteType)) {
                    warnings.add(new ImportWarning(ImportConflict.NOTETYPE_SHAPE_MISMATCH,
                            "NoteType shape mismatch: " + exportNoteType.name()));
                }
                noteTypeIdMapping.put(exportNoteType.id(), existingNoteType.getId().toString());
                // Map templates by name
                mapTemplates(exportNoteType, existingNoteType, templateIdMapping);
            } else {
                String newNoteTypeId = UUID.randomUUID().toString();
                noteTypeIdMapping.put(exportNoteType.id(), newNoteTypeId);
                noteTypesToCreate.add(exportNoteType);
                // Map templates to new IDs
                for (ExportCardTemplate template : exportNoteType.templates()) {
                    templateIdMapping.put(template.id(), UUID.randomUUID().toString());
                }
            }
        }

        // Build note mapping and detect duplicates
        Map<String, String> noteIdMapping = new HashMap<>();
        List<ExportNote> notesToCreate = new ArrayList<>();
        List<String> notesToSkip = new ArrayList<>();

        for (ExportNote exportNote : exportModel.notes()) {
            String noteTypeName = findExportNoteTypeName(exportNote.noteTypeId(), exportModel.noteTypes());
            if (noteTypeName == null) {
                warnings.add(new ImportWarning(ImportConflict.UNKNOWN_NOTETYPE,
                        "Unknown note type ID in note: " + exportNote.noteTypeId()));
                notesToSkip.add(exportNote.id());
                continue;
            }

            String contentHash = duplicateDetector.computeNoteHash(exportNote, noteTypeName);
            Note existingNote = findNoteByContentHash(contentHash, noteTypeName);

            if (existingNote != null) {
                // Duplicate found - skip and map to existing
                notesToSkip.add(exportNote.id());
                noteIdMapping.put(exportNote.id(), existingNote.id().toString());
            } else {
                String newNoteId = UUID.randomUUID().toString();
                noteIdMapping.put(exportNote.id(), newNoteId);
                notesToCreate.add(exportNote);
            }
        }

        // Build card list (remap IDs)
        List<ExportCard> cardsToCreate = new ArrayList<>(exportModel.cards());

        return new ImportPlan(
                decksToCreate,
                noteTypesToCreate,
                notesToCreate,
                Collections.emptyList(), // notesToUpdate - not implemented in MVP
                notesToSkip,
                cardsToCreate,
                noteIdMapping,
                deckIdMapping,
                noteTypeIdMapping,
                templateIdMapping,
                warnings
        );
    }

    private Deck findDeckByName(String name) {
        return deckRepository.findAll().stream()
                .filter(deck -> deck.name().equals(name))
                .findFirst()
                .orElse(null);
    }

    private NoteType findNoteTypeByName(String name) {
        return noteTypeRepository.findAll().stream()
                .filter(nt -> nt.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private boolean noteTypeShapeMatches(ExportNoteType exportNoteType, NoteType existing) {
        // Check fields match (case-insensitive)
        Set<String> exportFields = exportNoteType.fields().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        Set<String> existingFields = existing.getFields().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        return exportFields.equals(existingFields);
    }

    private void mapTemplates(ExportNoteType exportNoteType, NoteType existing,
                              Map<String, String> templateIdMapping) {
        for (ExportCardTemplate exportTemplate : exportNoteType.templates()) {
            CardTemplate existingTemplate = existing.getTemplates().stream()
                    .filter(t -> t.name().equalsIgnoreCase(exportTemplate.name()))
                    .findFirst()
                    .orElse(null);

            if (existingTemplate != null) {
                templateIdMapping.put(exportTemplate.id(), existingTemplate.id().toString());
            } else {
                templateIdMapping.put(exportTemplate.id(), UUID.randomUUID().toString());
            }
        }
    }

    private String findExportNoteTypeName(String noteTypeId, List<ExportNoteType> noteTypes) {
        return noteTypes.stream()
                .filter(nt -> nt.id().equals(noteTypeId))
                .map(ExportNoteType::name)
                .findFirst()
                .orElse(null);
    }

    private Note findNoteByContentHash(String contentHash, String noteTypeName) {
        // This is a simplified approach - in production, you'd want a hash index
        NoteType noteType = findNoteTypeByName(noteTypeName);
        if (noteType == null) {
            return null;
        }

        List<Note> candidateNotes = noteRepository.findByNoteTypeId(noteType.getId());
        for (Note note : candidateNotes) {
            String noteHash = duplicateDetector.computeNoteHash(note, noteType);
            if (noteHash.equals(contentHash)) {
                return note;
            }
        }
        return null;
    }

    private String generateDeckId() {
        // Returns a placeholder - actual ID assigned on save
        return "temp_deck_" + UUID.randomUUID();
    }
}
