package org.project.memospace.application.service.handler.deck;

import lombok.RequiredArgsConstructor;
import org.project.memospace.application.service.QueryHandler;
import org.project.memospace.application.service.query.deck.GetDeckQuery;
import org.project.memospace.domain.exception.DeckNotFoundException;
import org.project.memospace.domain.model.Deck;
import org.project.memospace.domain.port.DeckRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetDeckQueryHandler implements QueryHandler<GetDeckQuery, Deck> {

    private final DeckRepositoryPort deckRepository;

    @Override
    public Deck handle(GetDeckQuery query) {
        return deckRepository.findById(query.getDeckId())
                .orElseThrow(() -> new DeckNotFoundException(query.getDeckId()));
    }
}