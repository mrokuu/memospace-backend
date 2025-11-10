package org.project.memospace.application.service.query.card;

import lombok.Value;
import org.project.memospace.application.service.Query;
import org.project.memospace.domain.model.Card;

import java.util.List;

@Value
public class GetNextDueCardsQuery implements Query<List<Card>> {
    int limit;
    Long deckId;
}