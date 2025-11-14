package org.memospace.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * Value object representing a content-addressed media identifier (SHA-256 hash).
 * Canonical form: lowercase hex string of exactly 64 characters.
 */
@Builder(toBuilder = true)
public record MediaId(String value) {
    private static final int SHA256_HEX_LENGTH = 64;

    public static MediaId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("MediaId cannot be null or blank");
        }
        String normalized = value.toLowerCase().trim();
        if (normalized.length() != SHA256_HEX_LENGTH) {
            throw new IllegalArgumentException(
                    "MediaId must be exactly " + SHA256_HEX_LENGTH + " characters (SHA-256 hex)");
        }
        if (!normalized.matches("^[a-f0-9]{64}$")) {
            throw new IllegalArgumentException("MediaId must contain only lowercase hex characters");
        }
        return new MediaId(normalized);
    }

    @Override
    public @NotNull String toString() {
        return value;
    }
}
