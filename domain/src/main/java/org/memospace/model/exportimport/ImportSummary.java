package org.memospace.model.exportimport;

import lombok.Builder;

/**
 * Value object representing counts of imported entities.
 * Pure domain model with no framework dependencies.
 */
@Builder(toBuilder = true)
public record ImportSummary(int decksCreated, int noteTypesCreated, int notesCreated, int notesUpdated,
                            int notesSkipped, int cardsCreated, int cardsUpdated, int mediaLinked) {

    public static ImportSummary empty() {
        return new ImportSummary(0, 0, 0, 0, 0, 0, 0, 0);
    }
}
