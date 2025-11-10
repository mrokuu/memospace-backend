package org.project.memospace.application.service.handler.filtereddeck;

import lombok.RequiredArgsConstructor;
import org.project.memospace.application.service.QueryHandler;
import org.project.memospace.application.service.query.filtereddeck.GetFilteredDeckQuery;
import org.project.memospace.domain.exception.FilteredDeckNotFoundException;
import org.project.memospace.domain.model.FilteredDeck;
import org.project.memospace.domain.port.FilteredDeckRepositoryPort;
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
