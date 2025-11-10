package org.project.memospace.domain.common.value;

import java.util.Objects;

/**
 * Value object representing a normalized tag.
 * Tags are always lowercase and trimmed for consistency.
 */
public record Tag(String value) {
    public Tag(String value) {
        Objects.requireNonNull(value, "Tag value cannot be null");
        String normalized = value.trim().toLowerCase();

        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Tag cannot be empty");
        }
        if (normalized.length() > 64) {
            throw new IllegalArgumentException("Tag cannot exceed 64 characters: " + normalized);
        }

        this.value = normalized;
    }

    public static Tag of(String value) {
        return new Tag(value);
    }
}
