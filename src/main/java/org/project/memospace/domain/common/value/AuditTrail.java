package org.project.memospace.domain.common.value;

import lombok.Value;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Value object representing audit timestamps for domain entities.
 * Tracks when an entity was created and last updated.
 */
@Value
public class AuditTrail {
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public AuditTrail(LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");

        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("Updated at cannot be before created at");
        }
    }

    /**
     * Create a new audit trail with current timestamp.
     */
    public static AuditTrail now() {
        LocalDateTime now = LocalDateTime.now();
        return new AuditTrail(now, now);
    }

    /**
     * Create a new audit trail with the current time as updatedAt.
     */
    public AuditTrail withUpdatedNow() {
        return new AuditTrail(this.createdAt, LocalDateTime.now());
    }

    /**
     * Check if the entity was recently created (within the last N seconds).
     */
    public boolean isNewlyCreated(long maxAgeSeconds) {
        return createdAt.isAfter(LocalDateTime.now().minusSeconds(maxAgeSeconds));
    }
}
