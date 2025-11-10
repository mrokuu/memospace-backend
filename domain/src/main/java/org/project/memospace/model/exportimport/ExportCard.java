package org.project.memospace.domain.model.exportimport;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Value object representing a card in an export.
 * Pure domain model with no framework dependencies.
 *
 * @param id         UUID or Long as string for export
 * @param noteId     nullable for standalone cards
 * @param templateId nullable for standalone cards
 * @param type       e.g., "REVIEW", "NEW"
 */
@Builder(toBuilder = true)
public record ExportCard(String id, String deckId, String noteId, String templateId, String front, String back,
                         String type, double easeFactor, int intervalDays, int repetitions, int lapses,
                         LocalDateTime dueAt, boolean suspended, int flag, int position, LocalDateTime createdAt,
                         LocalDateTime updatedAt) {
    public ExportCard {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Card ID cannot be null or blank");
        }
        if (deckId == null || deckId.isBlank()) {
            throw new IllegalArgumentException("Deck ID cannot be null or blank");
        }
        if (front == null) {
            throw new IllegalArgumentException("Front cannot be null");
        }
        if (back == null) {
            throw new IllegalArgumentException("Back cannot be null");
        }
        if (dueAt == null) {
            throw new IllegalArgumentException("DueAt cannot be null");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("CreatedAt cannot be null");
        }
        if (updatedAt == null) {
            throw new IllegalArgumentException("UpdatedAt cannot be null");
        }

    }
}
