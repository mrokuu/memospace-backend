package org.project.memospace.domain.authoring.value;

import lombok.Value;

import java.util.Objects;

/**
 * Value object representing a card template name.
 * Template names identify different card generation templates within a NoteType.
 */
@Value
public class TemplateName {
    String value;

    private TemplateName(String value) {
        Objects.requireNonNull(value, "Template name cannot be null");
        String trimmed = value.trim();

        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Template name cannot be empty");
        }
        if (trimmed.length() > 64) {
            throw new IllegalArgumentException("Template name cannot exceed 64 characters: " + trimmed);
        }

        this.value = trimmed;
    }

    public static TemplateName of(String value) {
        return new TemplateName(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
