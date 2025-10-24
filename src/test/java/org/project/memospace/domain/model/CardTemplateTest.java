package org.project.memospace.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CardTemplateTest {

    @Test
    void shouldCreateBasicCardTemplate() {
        // When
        CardTemplate template = CardTemplate.create(
                "Card 1",
                "{{Front}}",
                "{{Back}}",
                false
        );

        // Then
        assertNotNull(template.id());
        assertEquals("Card 1", template.name());
        assertEquals("{{Front}}", template.frontTemplate());
        assertEquals("{{Back}}", template.backTemplate());
        assertFalse(template.isCloze());
    }

    @Test
    void shouldCreateClozeTemplate() {
        // When
        CardTemplate template = CardTemplate.create(
                "Cloze",
                "{{cloze:Text}}",
                "{{cloze:Text}}",
                true
        );

        // Then
        assertTrue(template.isCloze());
        assertEquals("Cloze", template.name());
    }

    @Test
    void shouldCreateTemplateWithSpecificId() {
        // Given
        UUID specificId = UUID.randomUUID();

        // When
        CardTemplate template = new CardTemplate(
                specificId,
                "Named Template",
                "Front: {{Question}}",
                "Back: {{Answer}}",
                false
        );

        // Then
        assertEquals(specificId, template.id());
        assertEquals("Named Template", template.name());
    }

    @Test
    void shouldRejectNullName() {
        // When / Then
        assertThrows(NullPointerException.class, () ->
                CardTemplate.create(null, "{{Front}}", "{{Back}}", false)
        );
    }

    @Test
    void shouldAllowEmptyName() {
        // When - Empty/blank names are allowed (validation at higher level if needed)
        CardTemplate template = CardTemplate.create("", "{{Front}}", "{{Back}}", false);

        // Then
        assertEquals("", template.name());
    }

    @Test
    void shouldRejectNullFrontTemplate() {
        // When / Then
        assertThrows(NullPointerException.class, () ->
                CardTemplate.create("Card", null, "{{Back}}", false)
        );
    }

    @Test
    void shouldRejectNullBackTemplate() {
        // When / Then
        assertThrows(NullPointerException.class, () ->
                CardTemplate.create("Card", "{{Front}}", null, false)
        );
    }

    @Test
    void shouldAllowEmptyTemplates() {
        // When
        CardTemplate template = CardTemplate.create("Empty", "", "", false);

        // Then
        assertEquals("", template.frontTemplate());
        assertEquals("", template.backTemplate());
    }

    @Test
    void shouldSupportMultilineTemplates() {
        // Given
        String multilineFront = "Line 1\n{{Field1}}\nLine 3";
        String multilineBack = "Answer:\n{{Field2}}\n\nExtra info";

        // When
        CardTemplate template = CardTemplate.create(
                "Multiline",
                multilineFront,
                multilineBack,
                false
        );

        // Then
        assertEquals(multilineFront, template.frontTemplate());
        assertEquals(multilineBack, template.backTemplate());
    }

    @Test
    void shouldSupportHTMLInTemplates() {
        // Given
        String htmlFront = "<div class='front'>{{Question}}</div>";
        String htmlBack = "<div class='back'><p>{{Answer}}</p></div>";

        // When
        CardTemplate template = CardTemplate.create(
                "HTML Template",
                htmlFront,
                htmlBack,
                false
        );

        // Then
        assertEquals(htmlFront, template.frontTemplate());
        assertEquals(htmlBack, template.backTemplate());
    }
}
