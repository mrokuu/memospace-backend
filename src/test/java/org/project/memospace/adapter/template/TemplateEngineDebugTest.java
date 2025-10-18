package org.project.memospace.adapter.template;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.memospace.domain.model.RenderedClozeCard;

import java.util.List;
import java.util.Map;

class TemplateEngineDebugTest {

    private TemplateEngineAdapter templateEngine;

    @BeforeEach
    void setUp() {
        templateEngine = new TemplateEngineAdapter();
    }

    @Test
    void debug_renderCloze_singleCloze() {
        String frontTemplate = "{{cloze:Text}}";
        String backTemplate = "{{Text}}";
        Map<String, String> fieldValues = Map.of(
                "Text", "The capital of {{c1::Poland}} is Warsaw."
        );

        System.out.println("Input:");
        System.out.println("Front template: " + frontTemplate);
        System.out.println("Back template: " + backTemplate);
        System.out.println("Field values: " + fieldValues);

        List<RenderedClozeCard> result = templateEngine.renderCloze(frontTemplate, backTemplate, fieldValues);

        System.out.println("\nOutput:");
        System.out.println("Number of cards: " + result.size());
        for (RenderedClozeCard card : result) {
            System.out.println("Cloze index: " + card.clozeIndex());
            System.out.println("Front: " + card.front());
            System.out.println("Back: " + card.back());
            System.out.println("---");
        }
    }
}