package org.memospace.dto;

import org.memospace.model.FilteredDeck;

public record RebuildFilteredDeckResult(
        FilteredDeck filteredDeck,
        int total) {
}
