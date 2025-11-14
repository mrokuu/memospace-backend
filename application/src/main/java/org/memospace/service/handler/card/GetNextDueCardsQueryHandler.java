package org.memospace.service.handler.card;

import lombok.RequiredArgsConstructor;
import org.memospace.service.QueryHandler;
import org.memospace.service.query.card.GetNextDueCardsQuery;
import org.memospace.model.Card;
import org.memospace.port.CardRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetNextDueCardsQueryHandler implements QueryHandler<GetNextDueCardsQuery, List<Card>> {

    private final CardRepositoryPort cardRepository;

    @Override
    public List<Card> handle(GetNextDueCardsQuery query) {
        if (query.getDeckId() != null) {
            return cardRepository.findDueCards(query.getDeckId(), query.getLimit());
        } else {
            return cardRepository.findDueCards(query.getLimit());
        }
    }
}