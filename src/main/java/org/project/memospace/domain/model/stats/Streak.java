package org.project.memospace.domain.model.stats;

import lombok.Builder;

/**
 * Represents review streak information.
 */
@Builder(toBuilder = true)
public record Streak(int current, int longest) {
}
