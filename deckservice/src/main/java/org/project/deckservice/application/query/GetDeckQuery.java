package org.project.deckservice.application.query;

import lombok.Value;
import org.project.deckservice.application.Query;
import org.project.deckservice.domain.model.Deck;

@Value
public class GetDeckQuery implements Query<Deck> {
    Long deckId;
}
