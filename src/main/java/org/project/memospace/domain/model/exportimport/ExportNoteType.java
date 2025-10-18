package org.project.memospace.domain.model.exportimport;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Value object representing a note type in an export.
 * Pure domain model with no framework dependencies.
 *
 * @param id UUID string
 */
@Builder(toBuilder = true)
public record ExportNoteType(String id, String name, List<String> fields, List<ExportCardTemplate> templates,
                             String css, LocalDateTime createdAt, LocalDateTime updatedAt) {
    public ExportNoteType(String id, String name, List<String> fields, List<ExportCardTemplate> templates,
                          String css, LocalDateTime createdAt, LocalDateTime updatedAt) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("NoteType ID cannot be null or blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("NoteType name cannot be null or blank");
        }
        if (fields == null || fields.isEmpty()) {
            throw new IllegalArgumentException("Fields cannot be null or empty");
        }
        if (templates == null || templates.isEmpty()) {
            throw new IllegalArgumentException("Templates cannot be null or empty");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("CreatedAt cannot be null");
        }
        if (updatedAt == null) {
            throw new IllegalArgumentException("UpdatedAt cannot be null");
        }

        this.id = id;
        this.name = name;
        this.fields = Collections.unmodifiableList(new ArrayList<>(fields));
        this.templates = Collections.unmodifiableList(new ArrayList<>(templates));
        this.css = css;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
