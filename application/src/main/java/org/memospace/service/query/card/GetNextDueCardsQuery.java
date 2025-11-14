package org.memospace.service.query.card;

import lombok.Value;
import org.memospace.service.Query;
import org.memospace.model.Card;

import java.util.List;

@Value
public class GetNextDueCardsQuery implements Query<List<Card>> {
    int limit;
    Long deckId;
}