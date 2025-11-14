package org.memospace.model.exportimport;

import lombok.Builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Domain model representing a plan for importing data.
 * Contains decisions about what to create, update, or skip.
 * Pure domain model with no framework dependencies.
 *
 * @param notesToSkip       note IDs from import that are duplicates
 * @param noteIdMapping     oldId -> newId/existingId
 * @param deckIdMapping     oldId -> newId/existingId
 * @param noteTypeIdMapping oldId -> newId/existingId
 * @param templateIdMapping oldId -> newId/existingId
 */
@Builder(toBuilder = true)
public record ImportPlan(List<ExportDeck> decksToCreate, List<ExportNoteType> noteTypesToCreate,
                         List<ExportNote> notesToCreate, List<ExportNote> notesToUpdate, List<String> notesToSkip,
                         List<ExportCard> cardsToCreate, Map<String, String> noteIdMapping,
                         Map<String, String> deckIdMapping, Map<String, String> noteTypeIdMapping,
                         Map<String, String> templateIdMapping, List<ImportWarning> warnings) {
    public ImportPlan(List<ExportDeck> decksToCreate, List<ExportNoteType> noteTypesToCreate,
                      List<ExportNote> notesToCreate, List<ExportNote> notesToUpdate, List<String> notesToSkip,
                      List<ExportCard> cardsToCreate, Map<String, String> noteIdMapping,
                      Map<String, String> deckIdMapping, Map<String, String> noteTypeIdMapping,
                      Map<String, String> templateIdMapping, List<ImportWarning> warnings) {
        if (decksToCreate == null) {
            throw new IllegalArgumentException("DecksToCreate cannot be null");
        }
        if (noteTypesToCreate == null) {
            throw new IllegalArgumentException("NoteTypesToCreate cannot be null");
        }
        if (notesToCreate == null) {
            throw new IllegalArgumentException("NotesToCreate cannot be null");
        }
        if (notesToUpdate == null) {
            throw new IllegalArgumentException("NotesToUpdate cannot be null");
        }
        if (notesToSkip == null) {
            throw new IllegalArgumentException("NotesToSkip cannot be null");
        }
        if (cardsToCreate == null) {
            throw new IllegalArgumentException("CardsToCreate cannot be null");
        }
        if (noteIdMapping == null) {
            throw new IllegalArgumentException("NoteIdMapping cannot be null");
        }
        if (deckIdMapping == null) {
            throw new IllegalArgumentException("DeckIdMapping cannot be null");
        }
        if (noteTypeIdMapping == null) {
            throw new IllegalArgumentException("NoteTypeIdMapping cannot be null");
        }
        if (templateIdMapping == null) {
            throw new IllegalArgumentException("TemplateIdMapping cannot be null");
        }
        if (warnings == null) {
            throw new IllegalArgumentException("Warnings cannot be null");
        }

        this.decksToCreate = Collections.unmodifiableList(new ArrayList<>(decksToCreate));
        this.noteTypesToCreate = Collections.unmodifiableList(new ArrayList<>(noteTypesToCreate));
        this.notesToCreate = Collections.unmodifiableList(new ArrayList<>(notesToCreate));
        this.notesToUpdate = Collections.unmodifiableList(new ArrayList<>(notesToUpdate));
        this.notesToSkip = Collections.unmodifiableList(new ArrayList<>(notesToSkip));
        this.cardsToCreate = Collections.unmodifiableList(new ArrayList<>(cardsToCreate));
        this.noteIdMapping = Collections.unmodifiableMap(new HashMap<>(noteIdMapping));
        this.deckIdMapping = Collections.unmodifiableMap(new HashMap<>(deckIdMapping));
        this.noteTypeIdMapping = Collections.unmodifiableMap(new HashMap<>(noteTypeIdMapping));
        this.templateIdMapping = Collections.unmodifiableMap(new HashMap<>(templateIdMapping));
        this.warnings = Collections.unmodifiableList(new ArrayList<>(warnings));
    }

    public static ImportPlan empty() {
        return new ImportPlan(
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyMap(),
                Collections.emptyMap(),
                Collections.emptyMap(),
                Collections.emptyMap(),
                Collections.emptyList()
        );
    }
}
