package org.memospace.common.value;

import java.util.Objects;

/**
 * Value object representing a unique identifier for a Deck aggregate.
 * Uses Long for compatibility with current database schema.
 * Provides type safety to prevent mixing different ID types.
 */
public record DeckId(Long value) {
    public DeckId(Long value) {
        this.value = Objects.requireNonNull(value, "Deck ID cannot be null");
        if (value <= 0) {
            throw new IllegalArgumentException("Deck ID must be positive");
        }
    }

    public static DeckId of(Long value) {
        return new DeckId(value);
    }

    public static DeckId fromString(String value) {
        Objects.requireNonNull(value, "Deck ID string cannot be null");
        return new DeckId(Long.parseLong(value));
    }
}
