package org.project.memospace.domain.common.value;

import lombok.Value;

import java.util.Objects;

/**
 * Value object representing a unique identifier for a Card aggregate.
 * Uses Long for compatibility with current database schema.
 * Provides type safety to prevent mixing different ID types.
 */
@Value
public class CardId {
    Long value;

    private CardId(Long value) {
        this.value = Objects.requireNonNull(value, "Card ID cannot be null");
        if (value <= 0) {
            throw new IllegalArgumentException("Card ID must be positive");
        }
    }

    public static CardId of(Long value) {
        return new CardId(value);
    }

    public static CardId fromString(String value) {
        Objects.requireNonNull(value, "Card ID string cannot be null");
        return new CardId(Long.parseLong(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
