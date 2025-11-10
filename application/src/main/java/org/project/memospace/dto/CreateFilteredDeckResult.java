package org.project.memospace.dto;

import org.project.memospace.domain.model.FilteredDeck;

public record CreateFilteredDeckResult(
        FilteredDeck filteredDeck,
        int total) {
}
