package org.memospace.model.exportimport;

import lombok.Builder;

/**
 * Value object representing a card template in an export.
 * Pure domain model with no framework dependencies.
 *
 * @param id UUID string
 */
@Builder(toBuilder = true)
public record ExportCardTemplate(String id, String name, String frontTemplate, String backTemplate, boolean isCloze) {
    public ExportCardTemplate {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Template ID cannot be null or blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Template name cannot be null or blank");
        }
        if (frontTemplate == null) {
            throw new IllegalArgumentException("Front template cannot be null");
        }
        if (backTemplate == null) {
            throw new IllegalArgumentException("Back template cannot be null");
        }

    }
}
