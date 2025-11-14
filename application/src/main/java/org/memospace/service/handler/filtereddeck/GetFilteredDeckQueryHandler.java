package org.memospace.service.handler.filtereddeck;

import lombok.RequiredArgsConstructor;
import org.memospace.service.QueryHandler;
import org.memospace.service.query.filtereddeck.GetFilteredDeckQuery;
import org.memospace.exception.FilteredDeckNotFoundException;
import org.memospace.model.FilteredDeck;
import org.memospace.port.FilteredDeckRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetFilteredDeckQueryHandler implements QueryHandler<GetFilteredDeckQuery, FilteredDeck> {

    private final FilteredDeckRepositoryPort filteredDeckRepository;

    @Override
    public FilteredDeck handle(GetFilteredDeckQuery query) {
        return filteredDeckRepository.findById(query.getFilteredDeckId())
                .orElseThrow(() -> new FilteredDeckNotFoundException(query.getFilteredDeckId()));
    }
}
