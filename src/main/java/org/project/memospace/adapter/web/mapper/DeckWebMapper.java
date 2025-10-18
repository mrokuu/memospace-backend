package org.project.memospace.adapter.web.mapper;

import org.project.memospace.adapter.web.dto.DeckDto;
import org.project.memospace.domain.model.Deck;
import org.springframework.stereotype.Component;

@Component
public class DeckWebMapper {

    public DeckDto toDto(Deck deck) {
        return new DeckDto(
                deck.id(),
                deck.name(),
                deck.description(),
                deck.createdAt()
        );
    }
}