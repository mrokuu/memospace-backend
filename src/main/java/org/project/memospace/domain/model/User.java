package org.project.memospace.domain.model;

import lombok.Builder;
import lombok.With;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Domain model representing a user account.
 * Framework-agnostic with no Spring Security dependencies.
 */
@Builder(toBuilder = true)
public record User(
    @With UUID id,
    Username username,
    String email,
    HashedPassword passwordHash,
    Set<Role> roles,
    boolean enabled,
    Instant createdAt,
    Instant updatedAt
) {
    public User {
        Objects.requireNonNull(username, "Username cannot be null");
        Objects.requireNonNull(passwordHash, "Password hash cannot be null");
        Objects.requireNonNull(roles, "Roles cannot be null");
        Objects.requireNonNull(createdAt, "CreatedAt cannot be null");

        if (roles.isEmpty()) {
            throw new IllegalArgumentException("User must have at least one role");
        }

        // Email can be null, but if present must be non-empty
        if (email != null && email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty if provided");
        }
    }

    public static User create(Username username, String email, HashedPassword passwordHash, Set<Role> roles) {
        Instant now = Instant.now();
        return User.builder()
                .id(null)
                .username(username)
                .email(email)
                .passwordHash(passwordHash)
                .roles(Set.copyOf(roles)) // Defensive copy
                .enabled(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public User updatePassword(HashedPassword newPasswordHash) {
        return toBuilder()
                .passwordHash(newPasswordHash)
                .updatedAt(Instant.now())
                .build();
    }

    public User updateEmail(String newEmail) {
        return toBuilder()
                .email(newEmail)
                .updatedAt(Instant.now())
                .build();
    }

    public User disable() {
        return toBuilder()
                .enabled(false)
                .updatedAt(Instant.now())
                .build();
    }

    public User enable() {
        return toBuilder()
                .enabled(true)
                .updatedAt(Instant.now())
                .build();
    }

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    public boolean isAdmin() {
        return roles.contains(Role.ROLE_ADMIN);
    }
}
