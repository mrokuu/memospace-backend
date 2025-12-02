package com.example.cardservice.application.query;

import com.example.cardservice.application.Query;
import com.example.cardservice.domain.model.Card;

public record GetCardQuery(Long cardId) implements Query<Card> {
}
