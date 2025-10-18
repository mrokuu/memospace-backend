package org.project.memospace.domain.model.exportimport;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Value object representing a note in an export.
 * Pure domain model with no framework dependencies.
 *
 * @param id         UUID string
 * @param deckId     UUID string
 * @param noteTypeId UUID string
 */
@Builder(toBuilder = true)
public record ExportNote(String id, String deckId, String noteTypeId, Map<String, String> fieldValues, Set<String> tags,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
    public ExportNote(String id, String deckId, String noteTypeId, Map<String, String> fieldValues,
                      Set<String> tags, LocalDateTime createdAt, LocalDateTime updatedAt) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Note ID cannot be null or blank");
        }
        if (deckId == null || deckId.isBlank()) {
            throw new IllegalArgumentException("Deck ID cannot be null or blank");
        }
        if (noteTypeId == null || noteTypeId.isBlank()) {
            throw new IllegalArgumentException("NoteType ID cannot be null or blank");
        }
        if (fieldValues == null) {
            throw new IllegalArgumentException("Field values cannot be null");
        }
        if (tags == null) {
            throw new IllegalArgumentException("Tags cannot be null");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("CreatedAt cannot be null");
        }
        if (updatedAt == null) {
            throw new IllegalArgumentException("UpdatedAt cannot be null");
        }

        this.id = id;
        this.deckId = deckId;
        this.noteTypeId = noteTypeId;
        this.fieldValues = Collections.unmodifiableMap(new HashMap<>(fieldValues));
        this.tags = Collections.unmodifiableSet(new HashSet<>(tags));
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
