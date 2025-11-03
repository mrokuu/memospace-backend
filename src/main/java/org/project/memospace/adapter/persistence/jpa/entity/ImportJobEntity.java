package org.project.memospace.adapter.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "import_jobs", indexes = {
        @Index(name = "idx_import_jobs_request_id", columnList = "request_id"),
        @Index(name = "idx_import_jobs_started_at", columnList = "started_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportJobEntity {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "request_id", length = 128)
    private String requestId;

    @Column(name = "source_type", length = 32, nullable = false)
    private String sourceType;

    @Column(name = "deck_id")
    private Long deckId;

    @Column(name = "note_type_id", columnDefinition = "BINARY(16)")
    private UUID noteTypeId;

    @Column(name = "file_checksum", length = 128)
    private String fileChecksum;

    @Column(name = "dry_run", nullable = false)
    private boolean dryRun;

    @Column(name = "forced", nullable = false)
    private boolean forced;

    @Column(name = "user_id", length = 128)
    private String userId;

    @Column(name = "deduplication_strategy", length = 32)
    private String deduplicationStrategy;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column(name = "status", length = 32, nullable = false)
    private String status;

    @Column(name = "total_lines")
    private Integer totalLines;

    @Column(name = "parsed_lines")
    private Integer parsedLines;

    @Column(name = "skipped_lines")
    private Integer skippedLines;

    @Column(name = "created_notes")
    private Integer createdNotes;

    @Column(name = "created_cards")
    private Integer createdCards;

    @Column(name = "error_count")
    private Integer errorCount;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
}
