package org.memospace.model;

import lombok.Builder;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Builder(toBuilder = true)
public record FilteredDeckMembership(UUID filteredDeckId, Long cardId, int position, Instant addedAt) {
    public FilteredDeckMembership(UUID filteredDeckId, Long cardId, int position, Instant addedAt) {
        this.filteredDeckId = Objects.requireNonNull(filteredDeckId, "FilteredDeckId cannot be null");
        this.cardId = Objects.requireNonNull(cardId, "CardId cannot be null");
        this.position = position;
        this.addedAt = Objects.requireNonNull(addedAt, "AddedAt cannot be null");
    }

    public static FilteredDeckMembership create(UUID filteredDeckId, Long cardId, int position, Instant now) {
        return new FilteredDeckMembership(filteredDeckId, cardId, position, now);
    }
}
