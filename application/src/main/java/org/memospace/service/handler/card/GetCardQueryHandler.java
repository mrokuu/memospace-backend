package org.memospace.service.handler.card;

import lombok.RequiredArgsConstructor;
import org.memospace.service.QueryHandler;
import org.memospace.service.query.card.GetCardQuery;
import org.memospace.exception.CardNotFoundException;
import org.memospace.model.Card;
import org.memospace.port.CardRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetCardQueryHandler implements QueryHandler<GetCardQuery, Card> {

    private final CardRepositoryPort cardRepository;

    @Override
    public Card handle(GetCardQuery query) {
        return cardRepository.findById(query.getCardId())
                .orElseThrow(() -> new CardNotFoundException(query.getCardId()));
    }
}