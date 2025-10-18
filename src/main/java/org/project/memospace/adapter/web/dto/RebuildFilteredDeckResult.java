package org.project.memospace.adapter.web.dto;

import org.project.memospace.domain.model.FilteredDeck;

public record RebuildFilteredDeckResult(
        FilteredDeck filteredDeck,
        int total) {
}
