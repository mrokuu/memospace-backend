package org.project.memospace.domain.model.exportimport;

import lombok.Builder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Domain model representing a complete export of flashcard data.
 * Pure domain model with no framework dependencies.
 */
@Builder(toBuilder = true)
public record ExportModel(int version, Instant exportedAt, List<ExportDeck> decks, List<ExportNoteType> noteTypes,
                          List<ExportNote> notes, List<ExportCard> cards, List<MediaManifestEntry> mediaManifest) {
    public ExportModel(int version, Instant exportedAt, List<ExportDeck> decks, List<ExportNoteType> noteTypes,
                       List<ExportNote> notes, List<ExportCard> cards, List<MediaManifestEntry> mediaManifest) {
        if (exportedAt == null) {
            throw new IllegalArgumentException("ExportedAt cannot be null");
        }
        if (decks == null) {
            throw new IllegalArgumentException("Decks cannot be null");
        }
        if (noteTypes == null) {
            throw new IllegalArgumentException("NoteTypes cannot be null");
        }
        if (notes == null) {
            throw new IllegalArgumentException("Notes cannot be null");
        }
        if (cards == null) {
            throw new IllegalArgumentException("Cards cannot be null");
        }
        if (mediaManifest == null) {
            throw new IllegalArgumentException("MediaManifest cannot be null");
        }

        this.version = version;
        this.exportedAt = exportedAt;
        this.decks = Collections.unmodifiableList(new ArrayList<>(decks));
        this.noteTypes = Collections.unmodifiableList(new ArrayList<>(noteTypes));
        this.notes = Collections.unmodifiableList(new ArrayList<>(notes));
        this.cards = Collections.unmodifiableList(new ArrayList<>(cards));
        this.mediaManifest = Collections.unmodifiableList(new ArrayList<>(mediaManifest));
    }
}
