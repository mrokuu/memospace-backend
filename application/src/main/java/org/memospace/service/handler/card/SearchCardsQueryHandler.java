package org.memospace.service.handler.card;

import lombok.RequiredArgsConstructor;
import org.memospace.service.QueryHandler;
import org.memospace.service.query.card.SearchCardsQuery;
import org.memospace.model.Card;
import org.memospace.port.CardRepositoryPort;
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