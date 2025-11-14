package org.memospace.authoring.value;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Value object representing the field values of a Note.
 * Encapsulates validation and provides type-safe access to field data.
 */
public record NoteFields(Map<FieldName, FieldValue> fields) {
    public NoteFields(Map<FieldName, FieldValue> fields) {
        this.fields = Collections.unmodifiableMap(new HashMap<>(
                Objects.requireNonNull(fields, "Fields cannot be null")
        ));
    }

    public static NoteFields of(Map<FieldName, FieldValue> fields) {
        return new NoteFields(fields);
    }

    /**
     * Create from string map (convenience method for legacy code).
     */
    public static NoteFields fromStringMap(Map<String, String> stringFields) {
        Map<FieldName, FieldValue> fields = stringFields.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> FieldName.of(e.getKey()),
                        e -> FieldValue.of(e.getValue())
                ));
        return new NoteFields(fields);
    }

    /**
     * Convert to string map (convenience method for legacy code).
     */
    public Map<String, String> toStringMap() {
        return fields.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().toString(),
                        e -> e.getValue().toString()
                ));
    }

    public FieldValue get(FieldName fieldName) {
        return fields.getOrDefault(fieldName, FieldValue.empty());
    }

    public boolean has(FieldName fieldName) {
        return fields.containsKey(fieldName);
    }

    public Set<FieldName> fieldNames() {
        return fields.keySet();
    }

    public int size() {
        return fields.size();
    }

    public boolean isEmpty() {
        return fields.isEmpty();
    }
}
