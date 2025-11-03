package org.project.memospace.adapter.persistence.jpa.mapper;

import org.project.memospace.adapter.persistence.jpa.entity.ImportJobEntity;
import org.project.memospace.domain.model.importer.DeduplicationStrategy;
import org.project.memospace.domain.model.importer.ImportJob;
import org.project.memospace.domain.model.importer.ImportJobStatus;
import org.springframework.stereotype.Component;

@Component
public class ImportJobJpaMapper {

    public ImportJobEntity toEntity(ImportJob job) {
        if (job == null) {
            return null;
        }
        return ImportJobEntity.builder()
                .id(job.id())
                .requestId(job.requestId())
                .sourceType(job.sourceType())
                .deckId(job.deckId())
                .noteTypeId(job.noteTypeId())
                .fileChecksum(job.fileChecksum())
                .dryRun(job.dryRun())
                .forced(job.forced())
                .userId(job.userId())
                .deduplicationStrategy(job.deduplicationStrategy() != null ? job.deduplicationStrategy().value() : null)
                .startedAt(job.startedAt())
                .finishedAt(job.finishedAt())
                .status(job.status() != null ? job.status().name() : ImportJobStatus.STARTED.name())
                .totalLines(job.totalLines())
                .parsedLines(job.parsedLines())
                .skippedLines(job.skippedLines())
                .createdNotes(job.createdNotes())
                .createdCards(job.createdCards())
                .errorCount(job.errorCount())
                .errorMessage(job.errorMessage())
                .build();
    }

    public ImportJob toDomain(ImportJobEntity entity) {
        if (entity == null) {
            return null;
        }
        return ImportJob.builder()
                .id(entity.getId())
                .requestId(entity.getRequestId())
                .sourceType(entity.getSourceType())
                .deckId(entity.getDeckId())
                .noteTypeId(entity.getNoteTypeId())
                .fileChecksum(entity.getFileChecksum())
                .dryRun(entity.isDryRun())
                .forced(entity.isForced())
                .userId(entity.getUserId())
                .deduplicationStrategy(entity.getDeduplicationStrategy() != null
                        ? DeduplicationStrategy.from(entity.getDeduplicationStrategy())
                        : null)
                .startedAt(entity.getStartedAt())
                .finishedAt(entity.getFinishedAt())
                .status(entity.getStatus() != null ? ImportJobStatus.valueOf(entity.getStatus()) : ImportJobStatus.STARTED)
                .totalLines(entity.getTotalLines() != null ? entity.getTotalLines() : 0)
                .parsedLines(entity.getParsedLines() != null ? entity.getParsedLines() : 0)
                .skippedLines(entity.getSkippedLines() != null ? entity.getSkippedLines() : 0)
                .createdNotes(entity.getCreatedNotes() != null ? entity.getCreatedNotes() : 0)
                .createdCards(entity.getCreatedCards() != null ? entity.getCreatedCards() : 0)
                .errorCount(entity.getErrorCount() != null ? entity.getErrorCount() : 0)
                .errorMessage(entity.getErrorMessage())
                .build();
    }
}
