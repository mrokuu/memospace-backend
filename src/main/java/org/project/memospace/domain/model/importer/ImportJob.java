package org.project.memospace.domain.model.importer;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder(toBuilder = true)
public record ImportJob(
        UUID id,
        String requestId,
        String sourceType,
        Long deckId,
        UUID noteTypeId,
        String fileChecksum,
        boolean dryRun,
        boolean forced,
        String userId,
        DeduplicationStrategy deduplicationStrategy,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        ImportJobStatus status,
        int totalLines,
        int parsedLines,
        int skippedLines,
        int createdNotes,
        int createdCards,
        int errorCount,
        String errorMessage
) {
    public ImportJob {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    public ImportJob markCompleted(LocalDateTime finished, int totalLines, int parsed, int skipped,
                                   int createdNotes, int createdCards, int errorCount) {
        return toBuilder()
                .finishedAt(finished)
                .status(ImportJobStatus.COMPLETED)
                .totalLines(totalLines)
                .parsedLines(parsed)
                .skippedLines(skipped)
                .createdNotes(createdNotes)
                .createdCards(createdCards)
                .errorCount(errorCount)
                .build();
    }

    public ImportJob markFailed(LocalDateTime finished, String message, int totalLines, int parsed,
                                int skipped, int createdNotes, int createdCards, int errorCount) {
        return toBuilder()
                .finishedAt(finished)
                .status(ImportJobStatus.FAILED)
                .errorMessage(message)
                .totalLines(totalLines)
                .parsedLines(parsed)
                .skippedLines(skipped)
                .createdNotes(createdNotes)
                .createdCards(createdCards)
                .errorCount(errorCount)
                .build();
    }

    public ImportJob markSkipped(LocalDateTime finished) {
        return toBuilder()
                .finishedAt(finished)
                .status(ImportJobStatus.SKIPPED)
                .build();
    }
}
