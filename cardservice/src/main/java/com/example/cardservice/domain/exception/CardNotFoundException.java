package com.example.cardservice.domain.exception;

public class CardNotFoundException extends RuntimeException {
    public CardNotFoundException(Long cardId) {
        super("Card not found with id: " + cardId);
    }
}
