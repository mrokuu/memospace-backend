package org.project.memospace.domain.model;

import java.util.Objects;

/**
 * Value object representing a username.
 * Usernames are case-insensitive and must be unique.
 */
public record Username(String value) {

    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 50;
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_-]+$";

    public Username {
        Objects.requireNonNull(value, "Username cannot be null");
        String trimmed = value.trim();

        if (trimmed.length() < MIN_LENGTH || trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                "Username must be between " + MIN_LENGTH + " and " + MAX_LENGTH + " characters"
            );
        }

        if (!trimmed.matches(USERNAME_PATTERN)) {
            throw new IllegalArgumentException(
                "Username can only contain alphanumeric characters, underscores, and hyphens"
            );
        }

        value = trimmed.toLowerCase(); // Normalize to lowercase
    }

    public static Username of(String value) {
        return new Username(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
