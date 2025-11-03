package org.project.memospace.domain.model.importer;

import lombok.Builder;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
public record TxtImportResult(
        boolean dryRun,
        Long deckId,
        UUID noteTypeId,
        DeduplicationStrategy deduplicationStrategy,
        boolean reusedPreviousImport,
        boolean forced,
        String fileChecksum,
        int totalLines,
        int parsed,
        int skipped,
        int createdNotes,
        int createdCards,
        List<TxtImportDuplicate> duplicates,
        List<TxtImportError> errors,
        List<TxtImportPreview> previewSample
) {
    public TxtImportResult {
        duplicates = duplicates != null ? List.copyOf(duplicates) : Collections.emptyList();
        errors = errors != null ? List.copyOf(errors) : Collections.emptyList();
        previewSample = previewSample != null ? List.copyOf(previewSample) : Collections.emptyList();
    }

    public int errorCount() {
        return errors.size();
    }
}
