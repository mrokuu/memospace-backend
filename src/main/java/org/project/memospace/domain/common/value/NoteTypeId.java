package org.project.memospace.domain.common.value;

import lombok.Value;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a unique identifier for a NoteType aggregate.
 * Provides type safety to prevent mixing different ID types.
 */
@Value
public class NoteTypeId {
    UUID value;

    private NoteTypeId(UUID value) {
        this.value = Objects.requireNonNull(value, "NoteType ID cannot be null");
    }

    public static NoteTypeId of(UUID value) {
        return new NoteTypeId(value);
    }

    public static NoteTypeId generate() {
        return new NoteTypeId(UUID.randomUUID());
    }

    public static NoteTypeId fromString(String value) {
        Objects.requireNonNull(value, "NoteType ID string cannot be null");
        return new NoteTypeId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
