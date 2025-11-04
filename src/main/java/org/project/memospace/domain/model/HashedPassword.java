package org.project.memospace.domain.model;

import java.util.Objects;

/**
 * Value object representing a hashed password.
 * This ensures we never accidentally store or compare plain text passwords.
 */
public record HashedPassword(String value) {

    public HashedPassword {
        Objects.requireNonNull(value, "Hashed password cannot be null");
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("Hashed password cannot be empty");
        }
    }

    public static HashedPassword of(String hashedValue) {
        return new HashedPassword(hashedValue);
    }
}
