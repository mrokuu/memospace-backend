package org.project.memospace.application.service.handler.note;

import org.project.memospace.domain.model.Card;
import org.project.memospace.domain.model.CardTemplate;
import org.project.memospace.domain.model.Note;
import org.project.memospace.domain.model.NoteType;
import org.project.memospace.domain.model.RenderedClozeCard;
import org.project.memospace.domain.port.TemplateEnginePort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NoteCardGenerationService {

    private final TemplateEnginePort templateEngine;

    public NoteCardGenerationService(TemplateEnginePort templateEngine) {
        this.templateEngine = templateEngine;
    }

    public List<Card> generateCardsForNote(Note note, NoteType noteType) {
        List<Card> cards = new ArrayList<>();

        for (CardTemplate template : noteType.getTemplates()) {
            if (template.isCloze()) {
                cards.addAll(generateClozeCards(note, template));
            } else {
                Card card = generateNormalCard(note, template);
                cards.add(card);
            }
        }

        return cards;
    }

    private List<Card> generateClozeCards(Note note, CardTemplate template) {
        List<Card> cards = new ArrayList<>();

        List<RenderedClozeCard> renderedCards = templateEngine.renderCloze(
                template.frontTemplate(),
                template.backTemplate(),
                note.fieldValues()
        );

        for (RenderedClozeCard renderedCard : renderedCards) {
            Card card = createCard(
                    note,
                    template,
                    renderedCard.front(),
                    renderedCard.back(),
                    renderedCard.clozeIndex()
            );
            cards.add(card);
        }

        return cards;
    }

    private Card generateNormalCard(Note note, CardTemplate template) {
        TemplateEnginePort.RenderedCard renderedCard = templateEngine.renderNormal(
                template.frontTemplate(),
                template.backTemplate(),
                note.fieldValues()
        );

        return createCard(
                note,
                template,
                renderedCard.getFront(),
                renderedCard.getBack(),
                null
        );
    }

    private Card createCard(Note note, CardTemplate template, String front, String back, Integer clozeIndex) {
        LocalDateTime now = LocalDateTime.now();
        List<String> tags = new ArrayList<>(note.tags());

        return new Card(
                null,  // id will be set by repository
                note.deckId(),
                front,
                back,
                tags,
                2.5,  // default ease factor
                1,    // default interval
                0,    // default repetitions
                now,  // due at (new cards are due immediately)
                now,  // created at
                now,  // updated at
                note.id(),
                template.id(),
                clozeIndex
        );
    }
}