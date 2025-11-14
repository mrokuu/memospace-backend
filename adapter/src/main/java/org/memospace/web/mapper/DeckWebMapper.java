package org.memospace.web.mapper;

import org.memospace.web.dto.DeckDto;
import org.memospace.model.Deck;
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