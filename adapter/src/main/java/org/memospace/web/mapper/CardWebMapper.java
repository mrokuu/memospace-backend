package org.memospace.web.mapper;

import org.memospace.web.dto.CardDto;
import org.memospace.model.Card;
import org.springframework.stereotype.Component;

@Component
public class CardWebMapper {

    public CardDto toDto(Card card) {
        return new CardDto(
                card.id(),
                card.deckId(),
                card.front(),
                card.back(),
                card.tags(),
                card.easeFactor(),
                card.intervalDays(),
                card.repetitions(),
                card.dueAt(),
                card.createdAt(),
                card.updatedAt(),
                card.noteId(),
                card.templateId(),
                card.clozeIndex()
        );
    }
}