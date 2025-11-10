package org.project.memospace.domain.model.stats;

import lombok.Builder;

import java.util.Objects;

/**
 * Defines the scope of decks for statistics queries.
 * Can be either a specific deck ID or all decks.
 */
@Builder(toBuilder = true)
public record DeckScope(Long deckId, boolean isAll) {

    public static DeckScope forDeck(Long deckId) {
        Objects.requireNonNull(deckId, "DeckId cannot be null");
        return new DeckScope(deckId, false);
    }

    public static DeckScope all() {
        return new DeckScope(null, true);
    }
}
