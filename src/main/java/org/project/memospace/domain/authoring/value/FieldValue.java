package org.project.memospace.domain.authoring.value;

import lombok.Value;

/**
 * Value object representing the content of a note field.
 * Field values have a maximum size to prevent database issues.
 */
@Value
public class FieldValue {
    String value;
    private static final int MAX_LENGTH = 16000;

    private FieldValue(String value) {
        // null is allowed for empty fields
        if (value != null && value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Field value cannot exceed %d characters (got %d)", MAX_LENGTH, value.length())
            );
        }
        this.value = value;
    }

    public static FieldValue of(String value) {
        return new FieldValue(value);
    }

    public static FieldValue empty() {
        return new FieldValue("");
    }

    @Override
    public String toString() {
        return value != null ? value : "";
    }

    public boolean isEmpty() {
        return value == null || value.isEmpty();
    }

    public boolean isBlank() {
        return value == null || value.isBlank();
    }
}
