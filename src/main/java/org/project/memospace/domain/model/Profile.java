package org.project.memospace.domain.model;

import lombok.Builder;
import lombok.With;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * User profile containing preferences and settings.
 */
@Builder(toBuilder = true)
public record Profile(
    @With UUID id,
    UUID userId,
    String displayName,
    String timezone,
    int dayCutoffHour,
    Long usn, // Update sequence number for future sync
    Long rev, // Revision for future sync
    String settingsJson, // JSON blob for future extensibility
    Instant createdAt,
    Instant updatedAt
) {
    private static final String DEFAULT_TIMEZONE = "Europe/Warsaw";
    private static final int DEFAULT_DAY_CUTOFF_HOUR = 4; // 4 AM

    public Profile {
        Objects.requireNonNull(userId, "UserId cannot be null");
        Objects.requireNonNull(timezone, "Timezone cannot be null");
        Objects.requireNonNull(createdAt, "CreatedAt cannot be null");
        Objects.requireNonNull(updatedAt, "UpdatedAt cannot be null");

        if (dayCutoffHour < 0 || dayCutoffHour > 23) {
            throw new IllegalArgumentException("Day cutoff hour must be between 0 and 23");
        }
    }

    public static Profile createDefault(UUID userId) {
        Instant now = Instant.now();
        return Profile.builder()
                .id(null)
                .userId(userId)
                .displayName(null)
                .timezone(DEFAULT_TIMEZONE)
                .dayCutoffHour(DEFAULT_DAY_CUTOFF_HOUR)
                .usn(0L)
                .rev(0L)
                .settingsJson(null)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public Profile updatePreferences(String displayName, String timezone, int dayCutoffHour) {
        return toBuilder()
                .displayName(displayName)
                .timezone(timezone)
                .dayCutoffHour(dayCutoffHour)
                .updatedAt(Instant.now())
                .build();
    }

    public Profile updateSettings(String settingsJson) {
        return toBuilder()
                .settingsJson(settingsJson)
                .updatedAt(Instant.now())
                .build();
    }

    public Profile incrementRevision() {
        return toBuilder()
                .rev((rev != null ? rev : 0L) + 1)
                .updatedAt(Instant.now())
                .build();
    }
}
