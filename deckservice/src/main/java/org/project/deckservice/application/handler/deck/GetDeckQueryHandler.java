package org.project.deckservice.application.handler.deck;

import lombok.RequiredArgsConstructor;
import org.project.deckservice.adapter.persistance.repository.DeckRepositoryAdapter;
import org.project.deckservice.application.QueryHandler;
import org.project.deckservice.application.query.GetDeckQuery;
import org.project.deckservice.domain.exception.DeckNotFoundException;
import org.project.deckservice.domain.model.Deck;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetDeckQueryHandler implements QueryHandler<GetDeckQuery, Deck> {

    private final DeckRepositoryAdapter deckRepositoryAdapter;

    @Override
    public Deck handle(GetDeckQuery query) {
        return deckRepositoryAdapter.findById(query.getDeckId())
                .orElseThrow(() -> new DeckNotFoundException(query.getDeckId()));
    }
}