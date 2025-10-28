package org.project.memospace.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RenderedClozeCardTest {

    @Test
    void shouldCreateRenderedClozeCardWithAllFields() {
        // Given
        int clozeIndex = 0;
        String front = "Paris is the capital of {{c1::France}}";
        String back = "Paris is the capital of <strong>France</strong>";

        // When
        RenderedClozeCard card = new RenderedClozeCard(clozeIndex, front, back);

        // Then
        assertEquals(clozeIndex, card.clozeIndex());
        assertEquals(front, card.front());
        assertEquals(back, card.back());
    }

    @Test
    void shouldThrowExceptionWhenFrontIsNull() {
        // When/Then
        assertThrows(NullPointerException.class, () ->
                new RenderedClozeCard(0, null, "back")
        );
    }

    @Test
    void shouldThrowExceptionWhenBackIsNull() {
        // When/Then
        assertThrows(NullPointerException.class, () ->
                new RenderedClozeCard(0, "front", null)
        );
    }

    @Test
    void shouldSupportBuilderPattern() {
        // When
        RenderedClozeCard card = RenderedClozeCard.builder()
                .clozeIndex(1)
                .front("What is the capital of France? {{c1::Paris}}")
                .back("What is the capital of France? <strong>Paris</strong>")
                .build();

        // Then
        assertEquals(1, card.clozeIndex());
        assertTrue(card.front().contains("{{c1::Paris}}"));
        assertTrue(card.back().contains("<strong>Paris</strong>"));
    }

    @Test
    void shouldSupportToBuilder() {
        // Given
        RenderedClozeCard original = RenderedClozeCard.builder()
                .clozeIndex(0)
                .front("original front")
                .back("original back")
                .build();

        // When
        RenderedClozeCard modified = original.toBuilder()
                .front("modified front")
                .build();

        // Then
        assertEquals(original.clozeIndex(), modified.clozeIndex());
        assertEquals("modified front", modified.front());
        assertEquals(original.back(), modified.back());
    }

    @Test
    void shouldHandleMultipleClozeIndexes() {
        // Given/When
        RenderedClozeCard card1 = new RenderedClozeCard(0, "front1", "back1");
        RenderedClozeCard card2 = new RenderedClozeCard(1, "front2", "back2");
        RenderedClozeCard card3 = new RenderedClozeCard(2, "front3", "back3");

        // Then
        assertEquals(0, card1.clozeIndex());
        assertEquals(1, card2.clozeIndex());
        assertEquals(2, card3.clozeIndex());
    }

    @Test
    void shouldHandleEmptyStrings() {
        // When
        RenderedClozeCard card = new RenderedClozeCard(0, "", "");

        // Then
        assertEquals("", card.front());
        assertEquals("", card.back());
    }

    @Test
    void shouldHandleLongContent() {
        // Given
        String longFront = "A".repeat(10000);
        String longBack = "B".repeat(10000);

        // When
        RenderedClozeCard card = new RenderedClozeCard(0, longFront, longBack);

        // Then
        assertEquals(10000, card.front().length());
        assertEquals(10000, card.back().length());
    }
}
