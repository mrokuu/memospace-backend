package org.memospace.model.stats;

import lombok.Builder;

import java.time.*;
import java.util.Objects;

/**
 * Represents timezone and day cutoff configuration for stats.
 * The cutoff hour determines when a "day" starts (e.g., 4 AM means the day starts at 4 AM).
 */
@Builder(toBuilder = true)
public record Cutoff(ZoneId timezone, int cutoffHour) {
    public Cutoff(ZoneId timezone, int cutoffHour) {
        this.timezone = Objects.requireNonNull(timezone, "Timezone cannot be null");
        if (cutoffHour < 0 || cutoffHour > 23) {
            throw new IllegalArgumentException("Cutoff hour must be between 0 and 23");
        }
        this.cutoffHour = cutoffHour;
    }

    public static Cutoff defaultCutoff() {
        return new Cutoff(ZoneId.of("UTC"), 4);
    }

    /**
     * Converts a LocalDateTime (in the timezone) to Instant at the start of that day with cutoff applied.
     */
    public Instant toInstantAtDayStart(LocalDate localDate) {
        LocalDateTime dateTimeAtCutoff = localDate.atTime(cutoffHour, 0);
        return dateTimeAtCutoff.atZone(timezone).toInstant();
    }

    /**
     * Converts an Instant to a LocalDate applying the cutoff logic.
     * Events between midnight and cutoff hour belong to the previous day.
     */
    public LocalDate toLocalDate(Instant instant) {
        ZonedDateTime zdt = instant.atZone(timezone);
        LocalDateTime ldt = zdt.toLocalDateTime();

        // If before cutoff hour, treat as previous day
        if (ldt.getHour() < cutoffHour) {
            return ldt.toLocalDate().minusDays(1);
        }
        return ldt.toLocalDate();
    }

    /**
     * Get "today" as a LocalDate using this cutoff.
     */
    public LocalDate today(Clock clock) {
        return toLocalDate(clock.instant());
    }
}
