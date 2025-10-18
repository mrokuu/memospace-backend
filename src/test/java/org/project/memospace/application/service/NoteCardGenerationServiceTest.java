package org.project.memospace.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.memospace.application.service.handler.note.NoteCardGenerationService;
import org.project.memospace.domain.model.Card;
import org.project.memospace.domain.model.CardTemplate;
import org.project.memospace.domain.model.Note;
import org.project.memospace.domain.model.NoteType;
import org.project.memospace.domain.model.RenderedClozeCard;
import org.project.memospace.domain.port.TemplateEnginePort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoteCardGenerationServiceTest {

    @Mock
    private TemplateEnginePort templateEngine;

    private NoteCardGenerationService service;

    @BeforeEach
    void setUp() {
        service = new NoteCardGenerationService(templateEngine);
    }

    @Test
    void generateCardsForNote_normalTemplates_shouldCreateOneCardPerTemplate() {
        // Given
        Note note = createTestNote();
        NoteType noteType = createNoteTypeWithNormalTemplates();

        when(templateEngine.renderNormal(eq("{{Front}}"), eq("{{Back}}"), any()))
                .thenReturn(new TemplateEnginePort.RenderedCard("Hello", "Cześć"));
        when(templateEngine.renderNormal(eq("{{Back}}"), eq("{{Front}}"), any()))
                .thenReturn(new TemplateEnginePort.RenderedCard("Cześć", "Hello"));

        // When
        List<Card> cards = service.generateCardsForNote(note, noteType);

        // Then
        assertEquals(2, cards.size());

        Card card1 = cards.get(0);
        assertEquals("Hello", card1.front());
        assertEquals("Cześć", card1.back());
        assertEquals(note.id(), card1.noteId());
        assertNotNull(card1.templateId());

        Card card2 = cards.get(1);
        assertEquals("Cześć", card2.front());
        assertEquals("Hello", card2.back());
        assertEquals(note.id(), card2.noteId());
        assertNotNull(card2.templateId());
    }

    @Test
    void generateCardsForNote_clozeTemplate_shouldCreateMultipleCards() {
        // Given
        Note note = createTestNote();
        NoteType noteType = createNoteTypeWithClozeTemplate();

        List<RenderedClozeCard> clozeCards = List.of(
                new RenderedClozeCard(1, "The capital of [...] is Warsaw.", "The capital of <strong>Poland</strong> is Warsaw."),
                new RenderedClozeCard(2, "The capital of Poland is [...].", "The capital of Poland is <strong>Warsaw</strong>.")
        );

        when(templateEngine.renderCloze(eq("{{cloze:Text}}"), eq("{{Text}}"), any()))
                .thenReturn(clozeCards);

        // When
        List<Card> cards = service.generateCardsForNote(note, noteType);

        // Then
        assertEquals(2, cards.size());

        Card card1 = cards.get(0);
        assertEquals("The capital of [...] is Warsaw.", card1.front());
        assertEquals(1, card1.clozeIndex());

        Card card2 = cards.get(1);
        assertEquals("The capital of Poland is [...].", card2.front());
        assertEquals(2, card2.clozeIndex());
    }

    @Test
    void generateCardsForNote_clozeTemplateNoClozes_shouldCreateNoCards() {
        // Given
        Note note = createTestNote();
        NoteType noteType = createNoteTypeWithClozeTemplate();

        when(templateEngine.renderCloze(eq("{{cloze:Text}}"), eq("{{Text}}"), any()))
                .thenReturn(List.of()); // No cloze deletions found

        // When
        List<Card> cards = service.generateCardsForNote(note, noteType);

        // Then
        assertEquals(0, cards.size());
    }

    @Test
    void generateCardsForNote_mixedTemplates_shouldCreateBothTypes() {
        // Given
        Note note = createTestNote();
        NoteType noteType = createNoteTypeWithMixedTemplates();

        when(templateEngine.renderNormal(eq("{{Front}}"), eq("{{Back}}"), any()))
                .thenReturn(new TemplateEnginePort.RenderedCard("Hello", "Cześć"));

        List<RenderedClozeCard> clozeCards = List.of(
                new RenderedClozeCard(1, "The capital of [...] is Warsaw.", "The capital of <strong>Poland</strong> is Warsaw.")
        );
        when(templateEngine.renderCloze(eq("{{cloze:Text}}"), eq("{{Text}}"), any()))
                .thenReturn(clozeCards);

        // When
        List<Card> cards = service.generateCardsForNote(note, noteType);

        // Then
        assertEquals(2, cards.size()); // 1 normal + 1 cloze

        // Check normal card
        Card normalCard = cards.stream()
                .filter(card -> card.clozeIndex() == null)
                .findFirst()
                .orElseThrow();
        assertEquals("Hello", normalCard.front());

        // Check cloze card
        Card clozeCard = cards.stream()
                .filter(card -> card.clozeIndex() != null)
                .findFirst()
                .orElseThrow();
        assertEquals(1, clozeCard.clozeIndex());
    }

    private Note createTestNote() {
        return new Note(
                UUID.randomUUID(),
                1L,
                UUID.randomUUID(),
                Map.of(
                        "Front", "Hello",
                        "Back", "Cześć",
                        "Text", "The capital of {{c1::Poland}} is {{c2::Warsaw}}."
                ),
                Set.of("test"),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private NoteType createNoteTypeWithNormalTemplates() {
        CardTemplate template1 = CardTemplate.create("Card 1", "{{Front}}", "{{Back}}", false);
        CardTemplate template2 = CardTemplate.create("Card 2", "{{Back}}", "{{Front}}", false);

        return NoteType.create(
                "Basic (and reversed card)",
                List.of("Front", "Back"),
                List.of(template1, template2),
                null
        );
    }

    private NoteType createNoteTypeWithClozeTemplate() {
        CardTemplate template = CardTemplate.create("Cloze", "{{cloze:Text}}", "{{Text}}", true);

        return NoteType.create(
                "Cloze",
                List.of("Text"),
                List.of(template),
                null
        );
    }

    private NoteType createNoteTypeWithMixedTemplates() {
        CardTemplate normalTemplate = CardTemplate.create("Normal", "{{Front}}", "{{Back}}", false);
        CardTemplate clozeTemplate = CardTemplate.create("Cloze", "{{cloze:Text}}", "{{Text}}", true);

        return NoteType.create(
                "Mixed",
                List.of("Front", "Back", "Text"),
                List.of(normalTemplate, clozeTemplate),
                null
        );
    }
}