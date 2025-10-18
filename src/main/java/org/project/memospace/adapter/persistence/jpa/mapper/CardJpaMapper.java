package org.project.memospace.adapter.persistence.jpa.mapper;

import org.project.memospace.adapter.persistence.jpa.entity.CardEntity;
import org.project.memospace.domain.model.Card;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class CardJpaMapper {

    public CardEntity toEntity(Card card) {
        return new CardEntity(
                card.id(),
                card.deckId(),
                card.front(),
                card.back(),
                String.join(",", card.tags()),
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

    public Card toDomain(CardEntity entity) {
        List<String> tags = entity.getTags() != null && !entity.getTags().isEmpty()
                ? Arrays.asList(entity.getTags().split(","))
                : List.of();

        return new Card(
                entity.getId(),
                entity.getDeckId(),
                entity.getFront(),
                entity.getBack(),
                tags,
                entity.getEaseFactor(),
                entity.getIntervalDays(),
                entity.getRepetitions(),
                entity.getDueAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getNoteId(),
                entity.getTemplateId(),
                entity.getClozeIndex()
        );
    }
}