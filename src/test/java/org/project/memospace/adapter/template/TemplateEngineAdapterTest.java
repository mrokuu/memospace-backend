package org.project.memospace.adapter.template;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.memospace.domain.model.RenderedClozeCard;
import org.project.memospace.domain.port.TemplateEnginePort;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TemplateEngineAdapterTest {

    private TemplateEngineAdapter templateEngine;

    @BeforeEach
    void setUp() {
        templateEngine = new TemplateEngineAdapter();
    }

    @Test
    void renderNormal_shouldReplaceFieldPlaceholders() {
        // Given
        String frontTemplate = "{{Front}}";
        String backTemplate = "{{Back}}";
        Map<String, String> fieldValues = Map.of(
                "Front", "Hello",
                "Back", "Cześć"
        );

        // When
        TemplateEnginePort.RenderedCard result = templateEngine.renderNormal(frontTemplate, backTemplate, fieldValues);

        // Then
        assertEquals("Hello", result.getFront());
        assertEquals("Cześć", result.getBack());
    }

    @Test
    void renderNormal_shouldHandleMissingFields() {
        // Given
        String frontTemplate = "{{Front}} - {{Missing}}";
        String backTemplate = "{{Back}}";
        Map<String, String> fieldValues = Map.of(
                "Front", "Hello",
                "Back", "Cześć"
        );

        // When
        TemplateEnginePort.RenderedCard result = templateEngine.renderNormal(frontTemplate, backTemplate, fieldValues);

        // Then
        assertEquals("Hello - ", result.getFront());
        assertEquals("Cześć", result.getBack());
    }

    @Test
    void renderCloze_singleCloze_shouldGenerateOneCard() {
        // Given
        String frontTemplate = "{{cloze:Text}}";
        String backTemplate = "{{cloze:Text}}";
        Map<String, String> fieldValues = Map.of(
                "Text", "The capital of {{c1::Poland}} is Warsaw."
        );

        // When
        List<RenderedClozeCard> result = templateEngine.renderCloze(frontTemplate, backTemplate, fieldValues);

        // Then
        assertEquals(1, result.size());
        RenderedClozeCard card = result.get(0);
        assertEquals(1, card.clozeIndex());
        assertTrue(card.front().contains("<span class=\"cloze\">[...]</span>"));
        assertTrue(card.front().contains("is Warsaw"));
        assertTrue(card.back().contains("<strong>Poland</strong>"));
    }

    @Test
    void renderCloze_multipleClozes_shouldGenerateMultipleCards() {
        // Given
        String frontTemplate = "{{cloze:Text}}";
        String backTemplate = "{{cloze:Text}}";
        Map<String, String> fieldValues = Map.of(
                "Text", "The capital of {{c1::Poland}} is {{c2::Warsaw}}."
        );

        // When
        List<RenderedClozeCard> result = templateEngine.renderCloze(frontTemplate, backTemplate, fieldValues);

        // Then
        assertEquals(2, result.size());

        // Check first cloze (Poland)
        RenderedClozeCard card1 = result.stream()
                .filter(card -> card.clozeIndex() == 1)
                .findFirst()
                .orElseThrow();
        assertTrue(card1.front().contains("<span class=\"cloze\">[...]</span>"));
        assertTrue(card1.front().contains("is Warsaw"));
        assertTrue(card1.back().contains("<strong>Poland</strong>"));

        // Check second cloze (Warsaw)
        RenderedClozeCard card2 = result.stream()
                .filter(card -> card.clozeIndex() == 2)
                .findFirst()
                .orElseThrow();
        assertTrue(card2.front().contains("The capital of Poland"));
        assertTrue(card2.front().contains("<span class=\"cloze\">[...]</span>"));
        assertTrue(card2.back().contains("<strong>Warsaw</strong>"));
    }

    @Test
    void renderCloze_withHint_shouldShowHintOnFront() {
        // Given
        String frontTemplate = "{{cloze:Text}}";
        String backTemplate = "{{cloze:Text}}";
        Map<String, String> fieldValues = Map.of(
                "Text", "The capital of {{c1::Poland::country}} is Warsaw."
        );

        // When
        List<RenderedClozeCard> result = templateEngine.renderCloze(frontTemplate, backTemplate, fieldValues);

        // Then
        assertEquals(1, result.size());
        RenderedClozeCard card = result.get(0);
        assertTrue(card.front().contains("[...] (country)"));
        assertTrue(card.back().contains("<strong>Poland</strong>"));
    }

    @Test
    void renderCloze_noClozeInField_shouldReturnEmptyList() {
        // Given
        String frontTemplate = "{{cloze:Text}}";
        String backTemplate = "{{Text}}";
        Map<String, String> fieldValues = Map.of(
                "Text", "This text has no cloze deletions."
        );

        // When
        List<RenderedClozeCard> result = templateEngine.renderCloze(frontTemplate, backTemplate, fieldValues);

        // Then
        assertEquals(0, result.size());
    }

    @Test
    void renderCloze_mixedFieldAndClozeReferences_shouldWork() {
        // Given
        String frontTemplate = "{{cloze:Text}}<br>{{Extra}}";
        String backTemplate = "{{Text}}<br>{{Extra}}";
        Map<String, String> fieldValues = Map.of(
                "Text", "The capital of {{c1::Poland}} is Warsaw.",
                "Extra", "Additional information"
        );

        // When
        List<RenderedClozeCard> result = templateEngine.renderCloze(frontTemplate, backTemplate, fieldValues);

        // Then
        assertEquals(1, result.size());
        RenderedClozeCard card = result.get(0);
        assertTrue(card.front().contains("Additional information"));
        assertTrue(card.back().contains("Additional information"));
        assertTrue(card.front().contains("<span class=\"cloze\">[...]</span>"));
    }
}