package org.project.memospace.application.service.query.card;

import lombok.Value;
import org.project.memospace.application.service.Query;
import org.project.memospace.domain.model.Card;

import java.util.List;

@Value
public class SearchCardsQuery implements Query<List<Card>> {
    Long deckId;
    String searchTerm;
    Boolean onlyDue;
    int page;
    int size;
}