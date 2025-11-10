package org.project.memospace.adapter.persistence.jpa.mapper;

import org.project.memospace.adapter.persistence.jpa.entity.CardEntity;
import org.project.memospace.domain.model.Card;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CardJpaMapper {

    public CardEntity toEntity(Card card) {
        String tagsString = card.tags() != null && !card.tags().isEmpty()
                ? String.join(",", card.tags())
                : null;

        return new CardEntity(
                card.id(),
                card.deckId(),
                card.front(),
                card.back(),
                tagsString,
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
        List<String> tags = parseTags(entity.getTags());

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

    /**
     * Parse comma-separated tags string into a list.
     * Handles null, empty strings, and filters out blank entries.
     *
     * @param tagsString comma-separated tags or null
     * @return list of non-blank tags
     */
    private List<String> parseTags(String tagsString) {
        if (tagsString == null || tagsString.trim().isEmpty()) {
            return List.of();
        }

        return Arrays.stream(tagsString.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toList());
    }
}