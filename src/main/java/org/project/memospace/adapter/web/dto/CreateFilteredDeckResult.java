package org.project.memospace.adapter.web.dto;

import org.project.memospace.domain.model.FilteredDeck;

public record CreateFilteredDeckResult(
        FilteredDeck filteredDeck,
        int total) {
}
