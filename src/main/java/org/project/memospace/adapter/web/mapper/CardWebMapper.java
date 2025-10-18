package org.project.memospace.adapter.web.mapper;

import org.project.memospace.adapter.web.dto.CardDto;
import org.project.memospace.domain.model.Card;
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