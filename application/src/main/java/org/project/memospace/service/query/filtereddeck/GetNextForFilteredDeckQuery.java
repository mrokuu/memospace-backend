package org.project.memospace.application.service.query.filtereddeck;

import lombok.Value;
import org.project.memospace.application.service.Query;
import org.project.memospace.domain.model.Card;

import java.util.List;
import java.util.UUID;

@Value
public class GetNextForFilteredDeckQuery implements Query<List<Card>> {
    UUID filteredDeckId;
    int limit;
}
