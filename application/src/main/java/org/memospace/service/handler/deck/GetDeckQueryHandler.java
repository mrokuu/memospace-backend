package org.memospace.service.handler.deck;

import lombok.RequiredArgsConstructor;
import org.memospace.service.QueryHandler;
import org.memospace.service.query.deck.GetDeckQuery;
import org.memospace.exception.DeckNotFoundException;
import org.memospace.model.Deck;
import org.memospace.port.DeckRepositoryPort;
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