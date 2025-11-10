package org.project.memospace.domain.exception;

public class DeckNotFoundException extends RuntimeException {
    public DeckNotFoundException(Long deckId) {
        super("Deck not found with id: " + deckId);
    }
}