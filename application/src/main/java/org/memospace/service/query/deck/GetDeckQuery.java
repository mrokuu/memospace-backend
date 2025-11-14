package org.memospace.service.query.deck;

import lombok.Value;
import org.memospace.service.Query;
import org.memospace.model.Deck;

@Value
public class GetDeckQuery implements Query<Deck> {
    Long deckId;
}