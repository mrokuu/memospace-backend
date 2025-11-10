package org.project.memospace.application.service.query.deck;

import lombok.Value;
import org.project.memospace.application.service.Query;
import org.project.memospace.domain.model.Deck;

@Value
public class GetDeckQuery implements Query<Deck> {
    Long deckId;
}