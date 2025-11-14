package org.memospace.service.query.filtereddeck;

import lombok.Value;
import org.memospace.service.Query;
import org.memospace.model.FilteredDeck;

import java.util.UUID;

@Value
public class GetFilteredDeckQuery implements Query<FilteredDeck> {
    UUID filteredDeckId;
}
