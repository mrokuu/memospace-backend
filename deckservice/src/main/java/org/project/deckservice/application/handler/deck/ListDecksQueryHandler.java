package org.project.deckservice.application.handler.deck;

import lombok.RequiredArgsConstructor;
import org.project.deckservice.adapter.persistance.repository.DeckRepositoryAdapter;
import org.project.deckservice.application.QueryHandler;
import org.project.deckservice.application.query.ListDecksQuery;
import org.project.deckservice.domain.model.Deck;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListDecksQueryHandler implements QueryHandler<ListDecksQuery, List<Deck>> {

    private final DeckRepositoryAdapter deckRepositoryAdapter;

    @Override
    public List<Deck> handle(ListDecksQuery query) {
        return deckRepositoryAdapter.findAll();
    }
}