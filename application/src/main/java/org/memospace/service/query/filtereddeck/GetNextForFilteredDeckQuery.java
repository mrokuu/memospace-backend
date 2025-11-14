package org.memospace.service.query.filtereddeck;

import lombok.Value;
import org.memospace.service.Query;
import org.memospace.model.Card;

import java.util.List;
import java.util.UUID;

@Value
public class GetNextForFilteredDeckQuery implements Query<List<Card>> {
    UUID filteredDeckId;
    int limit;
}
