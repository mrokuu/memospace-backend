package org.project.memospace.domain.authoring.value;

import lombok.Value;

import java.util.Objects;

/**
 * Value object representing a field name in a NoteType.
 * Field names are case-sensitive identifiers used in templates.
 */
@Value
public class FieldName {
    String value;

    private FieldName(String value) {
        Objects.requireNonNull(value, "Field name cannot be null");
        String trimmed = value.trim();

        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Field name cannot be empty");
        }
        if (trimmed.length() > 64) {
            throw new IllegalArgumentException("Field name cannot exceed 64 characters: " + trimmed);
        }

        this.value = trimmed;
    }

    public static FieldName of(String value) {
        return new FieldName(value);
    }

    @Override
    public String toString() {
        return value;
    }

    /**
     * Check if this field name equals another (case-insensitive).
     */
    public boolean equalsIgnoreCase(FieldName other) {
        return this.value.equalsIgnoreCase(other.value);
    }
}
