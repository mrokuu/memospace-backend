package org.project.memospace.domain.authoring.value;

/**
 * Value object representing the content of a note field.
 * Field values have a maximum size to prevent database issues.
 */
public record FieldValue(String value) {
    private static final int MAX_LENGTH = 16000;

    public FieldValue {
        // null is allowed for empty fields
        if (value != null && value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Field value cannot exceed %d characters (got %d)", MAX_LENGTH, value.length())
            );
        }
    }

    public static FieldValue of(String value) {
        return new FieldValue(value);
    }

    public static FieldValue empty() {
        return new FieldValue("");
    }

    public boolean isEmpty() {
        return value == null || value.isEmpty();
    }

    public boolean isBlank() {
        return value == null || value.isBlank();
    }
}
