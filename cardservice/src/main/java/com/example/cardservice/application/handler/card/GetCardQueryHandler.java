package com.example.cardservice.application.handler.card;

import lombok.RequiredArgsConstructor;
import org.project.memospace.application.cqrs.QueryHandler;
import org.project.memospace.application.cqrs.query.card.GetCardQuery;
import org.project.memospace.domain.exception.CardNotFoundException;
import org.project.memospace.domain.model.Card;
import org.project.memospace.domain.port.CardRepositoryPort;
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