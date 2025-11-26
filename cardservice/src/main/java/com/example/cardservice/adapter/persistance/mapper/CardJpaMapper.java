package com.example.cardservice.adapter.persistance.mapper;

import com.example.cardservice.domain.entity.CardEntity;
import com.example.cardservice.domain.model.Card;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class CardJpaMapper {

    public CardEntity toEntity(Card card) {
        return new CardEntity(
                card.getId(),
                card.getDeckId(),
                card.getFront(),
                card.getBack(),
                String.join(",", card.getTags()),
                card.getEaseFactor(),
                card.getIntervalDays(),
                card.getRepetitions(),
                card.getDueAt(),
                card.getCreatedAt(),
                card.getUpdatedAt(),
                card.getNoteId(),
                card.getTemplateId(),
                card.getClozeIndex()
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
