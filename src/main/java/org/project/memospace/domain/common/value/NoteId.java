package org.project.memospace.domain.common.value;

import lombok.Value;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a unique identifier for a Note aggregate.
 * Provides type safety to prevent mixing different ID types.
 */
@Value
public class NoteId {
    UUID value;

    private NoteId(UUID value) {
        this.value = Objects.requireNonNull(value, "Note ID cannot be null");
    }

    public static NoteId of(UUID value) {
        return new NoteId(value);
    }

    public static NoteId generate() {
        return new NoteId(UUID.randomUUID());
    }

    public static NoteId fromString(String value) {
        Objects.requireNonNull(value, "Note ID string cannot be null");
        return new NoteId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
