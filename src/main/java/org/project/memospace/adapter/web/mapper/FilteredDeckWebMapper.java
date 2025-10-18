package org.project.memospace.adapter.web.mapper;

import org.project.memospace.adapter.web.dto.CreateFilteredDeckResponse;
import org.project.memospace.adapter.web.dto.FilteredDeckDto;
import org.project.memospace.adapter.web.dto.RebuildFilteredDeckResponse;
import org.project.memospace.domain.model.FilteredDeck;
import org.springframework.stereotype.Component;

@Component
public class FilteredDeckWebMapper {

    public FilteredDeckDto toDto(FilteredDeck filteredDeck) {
        return new FilteredDeckDto(
                filteredDeck.id(),
                filteredDeck.name(),
                filteredDeck.ownerDeckId(),
                filteredDeck.query(),
                filteredDeck.limit(),
                filteredDeck.mode(),
                filteredDeck.returnPolicy(),
                filteredDeck.createdAt(),
                filteredDeck.expiresAt(),
                filteredDeck.lastBuiltAt()
        );
    }

    public CreateFilteredDeckResponse toCreateResponse(FilteredDeck filteredDeck, int total) {
        return new CreateFilteredDeckResponse(toDto(filteredDeck), total);
    }

    public RebuildFilteredDeckResponse toRebuildResponse(FilteredDeck filteredDeck, int total) {
        return new RebuildFilteredDeckResponse(toDto(filteredDeck), total);
    }
}
