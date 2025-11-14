package org.memospace.service.handler.deck;

import lombok.RequiredArgsConstructor;
import org.memospace.service.QueryHandler;
import org.memospace.service.query.deck.ListDecksQuery;
import org.memospace.model.Deck;
import org.memospace.port.DeckRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListDecksQueryHandler implements QueryHandler<ListDecksQuery, List<Deck>> {

    private final DeckRepositoryPort deckRepository;

    @Override
    public List<Deck> handle(ListDecksQuery query) {
        return deckRepository.findAll();
    }
}