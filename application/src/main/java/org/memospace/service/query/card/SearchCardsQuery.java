package org.memospace.service.query.card;

import lombok.Value;
import org.memospace.service.Query;
import org.memospace.model.Card;

import java.util.List;

@Value
public class SearchCardsQuery implements Query<List<Card>> {
    Long deckId;
    String searchTerm;
    Boolean onlyDue;
    int page;
    int size;
}