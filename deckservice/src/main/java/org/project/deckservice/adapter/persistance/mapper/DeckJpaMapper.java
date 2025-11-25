package org.project.deckservice.adapter.persistance.mapper;

import org.project.deckservice.domain.entity.DeckEntity;
import org.project.deckservice.domain.model.Deck;
import org.springframework.stereotype.Component;

@Component
public class DeckJpaMapper {

    public DeckEntity toEntity(Deck deck) {
        return new DeckEntity(
                deck.getId(),
                deck.getName(),
                deck.getDescription(),
                deck.getCreatedAt()
        );
    }

    public Deck toDomain(DeckEntity entity) {
        return new Deck(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getCreatedAt()
        );
    }
}
