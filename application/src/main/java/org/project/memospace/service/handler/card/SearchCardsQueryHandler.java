package org.project.memospace.application.service.handler.card;

import lombok.RequiredArgsConstructor;
import org.project.memospace.application.service.QueryHandler;
import org.project.memospace.application.service.query.card.SearchCardsQuery;
import org.project.memospace.domain.model.Card;
import org.project.memospace.domain.port.CardRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchCardsQueryHandler implements QueryHandler<SearchCardsQuery, List<Card>> {

    private final CardRepositoryPort cardRepository;

    @Override
    public List<Card> handle(SearchCardsQuery query) {
        return cardRepository.searchCards(
                query.getDeckId(),
                query.getSearchTerm(),
                query.getOnlyDue(),
                query.getPage(),
                query.getSize()
        );
    }
}