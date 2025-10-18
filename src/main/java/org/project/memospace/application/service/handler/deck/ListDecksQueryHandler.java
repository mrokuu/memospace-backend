package org.project.memospace.application.service.handler.deck;

import lombok.RequiredArgsConstructor;
import org.project.memospace.application.service.QueryHandler;
import org.project.memospace.application.service.query.deck.ListDecksQuery;
import org.project.memospace.domain.model.Deck;
import org.project.memospace.domain.port.DeckRepositoryPort;
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