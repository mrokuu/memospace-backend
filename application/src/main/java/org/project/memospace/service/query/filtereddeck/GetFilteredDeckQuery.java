package org.project.memospace.application.service.query.filtereddeck;

import lombok.Value;
import org.project.memospace.application.service.Query;
import org.project.memospace.domain.model.FilteredDeck;

import java.util.UUID;

@Value
public class GetFilteredDeckQuery implements Query<FilteredDeck> {
    UUID filteredDeckId;
}
