package org.memospace.dto;

import org.memospace.model.FilteredDeck;

public record CreateFilteredDeckResult(
        FilteredDeck filteredDeck,
        int total) {
}
