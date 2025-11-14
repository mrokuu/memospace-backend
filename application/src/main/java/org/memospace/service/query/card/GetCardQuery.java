package org.memospace.service.query.card;

import lombok.Value;
import org.memospace.service.Query;
import org.memospace.model.Card;

@Value
public class GetCardQuery implements Query<Card> {
    Long cardId;
}