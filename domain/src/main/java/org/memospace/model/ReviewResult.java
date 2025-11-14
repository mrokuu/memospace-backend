package org.memospace.model;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Objects;

@Builder(toBuilder = true)
public record ReviewResult(int quality, LocalDateTime reviewedAt, Integer msSpent) {
    public ReviewResult(int quality, LocalDateTime reviewedAt, Integer msSpent) {
        if (quality < 0 || quality > 4) {
            throw new IllegalArgumentException("Quality must be between 0 and 4, but was: " + quality);
        }
        this.quality = quality;
        this.reviewedAt = Objects.requireNonNull(reviewedAt, "ReviewedAt cannot be null");
        this.msSpent = msSpent;
    }

    public static ReviewResult create(int quality, Integer msSpent) {
        return new ReviewResult(quality, LocalDateTime.now(), msSpent);
    }

    public static ReviewResult create(int quality) {
        return new ReviewResult(quality, LocalDateTime.now(), null);
    }
}