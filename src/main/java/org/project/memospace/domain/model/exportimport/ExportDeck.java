package org.project.memospace.domain.model.exportimport;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Value object representing a deck in an export.
 * Pure domain model with no framework dependencies.
 *
 * @param id UUID string for export
 */
@Builder(toBuilder = true)
public record ExportDeck(String id, String name, String description, LocalDateTime createdAt) {
    public ExportDeck {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Deck ID cannot be null or blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Deck name cannot be null or blank");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("CreatedAt cannot be null");
        }

    }
}
