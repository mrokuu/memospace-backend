package org.project.memospace.domain.model;

import lombok.Builder;
import lombok.With;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain model for refresh tokens.
 * Stored server-side to allow revocation.
 */
@Builder(toBuilder = true)
public record RefreshToken(
    @With UUID id, // This is the jti (JWT ID)
    UUID userId,
    Instant expiresAt,
    boolean revoked,
    Instant issuedAt,
    String ip,
    String userAgent
) {
    public RefreshToken {
        Objects.requireNonNull(id, "Token ID (jti) cannot be null");
        Objects.requireNonNull(userId, "UserId cannot be null");
        Objects.requireNonNull(expiresAt, "ExpiresAt cannot be null");
        Objects.requireNonNull(issuedAt, "IssuedAt cannot be null");
    }

    public static RefreshToken create(UUID userId, Instant expiresAt, String ip, String userAgent) {
        return RefreshToken.builder()
                .id(UUID.randomUUID()) // Generate jti
                .userId(userId)
                .expiresAt(expiresAt)
                .revoked(false)
                .issuedAt(Instant.now())
                .ip(ip)
                .userAgent(userAgent)
                .build();
    }

    public RefreshToken revoke() {
        return toBuilder()
                .revoked(true)
                .build();
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !revoked && !isExpired();
    }
}
