package org.project.memospace.domain.model;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class NoteType {
    UUID id;
    String name;
    @Singular
    List<String> fields;
    @Singular
    List<CardTemplate> templates;
    String css;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public NoteType(UUID id, String name, List<String> fields, List<CardTemplate> templates,
                    String css, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.fields = Collections.unmodifiableList(new ArrayList<>(Objects.requireNonNull(fields, "Fields cannot be null")));
        this.templates = Collections.unmodifiableList(new ArrayList<>(Objects.requireNonNull(templates, "Templates cannot be null")));
        this.css = css;
        this.createdAt = Objects.requireNonNull(createdAt, "CreatedAt cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "UpdatedAt cannot be null");

        validateFields();
        validateTemplates();
    }

    public static NoteType create(String name, List<String> fields, List<CardTemplate> templates, String css) {
        LocalDateTime now = LocalDateTime.now();
        return new NoteType(UUID.randomUUID(), name, fields, templates, css, now, now);
    }

    public NoteType update(String name, List<String> fields, List<CardTemplate> templates, String css) {
        return toBuilder()
                .name(name)
                .fields(fields)
                .templates(templates)
                .css(css)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private void validateFields() {
        if (fields.isEmpty()) {
            throw new IllegalArgumentException("Fields cannot be empty");
        }

        for (String field : fields) {
            if (field == null || field.trim().isEmpty()) {
                throw new IllegalArgumentException("Field cannot be null or empty");
            }
            if (field.length() > 64) {
                throw new IllegalArgumentException("Field name cannot exceed 64 characters: " + field);
            }
        }

        // Check for duplicate fields (case-insensitive)
        long uniqueFields = fields.stream()
                .map(String::toLowerCase)
                .distinct()
                .count();
        if (uniqueFields != fields.size()) {
            throw new IllegalArgumentException("Duplicate field names are not allowed");
        }
    }

    private void validateTemplates() {
        if (templates.isEmpty()) {
            throw new IllegalArgumentException("Templates cannot be empty");
        }

        // Validate that all field references in templates exist
        for (CardTemplate template : templates) {
            validateTemplateFieldReferences(template);
        }
    }

    private void validateTemplateFieldReferences(CardTemplate template) {
        validateTemplateString(template.frontTemplate());
        validateTemplateString(template.backTemplate());
    }

    private void validateTemplateString(String templateString) {
        // Check for {{FieldName}} references
        String[] parts = templateString.split("\\{\\{");
        for (int i = 1; i < parts.length; i++) {
            int endIndex = parts[i].indexOf("}}");
            if (endIndex > 0) {
                String reference = parts[i].substring(0, endIndex);

                // Handle {{cloze:FieldName}} syntax
                if (reference.startsWith("cloze:")) {
                    String fieldName = reference.substring(6);
                    if (!fields.contains(fieldName)) {
                        throw new IllegalArgumentException("Template references unknown field: " + fieldName);
                    }
                } else if (!fields.contains(reference)) {
                    throw new IllegalArgumentException("Template references unknown field: " + reference);
                }
            }
        }
    }
}