package org.project.memospace.adapter.persistence.jpa.mapper;

import org.project.memospace.adapter.persistence.jpa.entity.DeckEntity;
import org.project.memospace.domain.model.Deck;
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