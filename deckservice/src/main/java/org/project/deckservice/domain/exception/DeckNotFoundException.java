package org.project.deckservice.domain.exception;

public class DeckNotFoundException extends RuntimeException {
    public DeckNotFoundException(Long deckId) {
        super("Deck not found with id: " + deckId);
    }
}
