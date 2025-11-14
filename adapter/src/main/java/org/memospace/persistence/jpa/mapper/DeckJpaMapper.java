package org.memospace.persistence.jpa.mapper;

import org.memospace.persistence.jpa.entity.DeckEntity;
import org.memospace.model.Deck;
import org.springframework.stereotype.Component;

@Component
public class DeckJpaMapper {

    public DeckEntity toEntity(Deck deck) {
        return new DeckEntity(
                deck.id(),
                deck.name(),
                deck.description(),
                deck.createdAt()
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