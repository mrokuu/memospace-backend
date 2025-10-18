package org.project.memospace.application.service.query.card;

import lombok.Value;
import org.project.memospace.application.service.Query;
import org.project.memospace.domain.model.Card;

@Value
public class GetCardQuery implements Query<Card> {
    Long cardId;
}