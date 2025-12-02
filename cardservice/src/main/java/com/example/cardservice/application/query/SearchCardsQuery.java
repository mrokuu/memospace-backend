package com.example.cardservice.application.query;

import com.example.cardservice.application.Query;
import com.example.cardservice.domain.model.Card;

import java.util.List;

public record SearchCardsQuery(Long deckId, String searchTerm, Boolean onlyDue, int page,
                               int size) implements Query<List<Card>> {
}
