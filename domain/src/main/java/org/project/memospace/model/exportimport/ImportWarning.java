package org.project.memospace.domain.model.exportimport;

import lombok.Builder;

/**
 * Value object representing a warning during import.
 * Pure domain model with no framework dependencies.
 */
@Builder(toBuilder = true)
public record ImportWarning(ImportConflict code, String detail) {
    public ImportWarning {
        if (code == null) {
            throw new IllegalArgumentException("Code cannot be null");
        }
        if (detail == null || detail.isBlank()) {
            throw new IllegalArgumentException("Detail cannot be null or blank");
        }

    }
}
