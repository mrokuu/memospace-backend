package org.memospace.model.stats;

import lombok.Builder;

/**
 * Represents review streak information.
 */
@Builder(toBuilder = true)
public record Streak(int current, int longest) {
}
