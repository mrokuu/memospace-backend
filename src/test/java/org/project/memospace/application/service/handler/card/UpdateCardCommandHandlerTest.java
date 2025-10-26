package org.project.memospace.application.service.handler.card;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.memospace.application.service.command.card.UpdateCardCommand;
import org.project.memospace.domain.exception.CardNotFoundException;
import org.project.memospace.domain.model.Card;
import org.project.memospace.domain.port.CardRepositoryPort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.project.memospace.util.TestFixtures.*;

@ExtendWith(MockitoExtension.class)
class UpdateCardCommandHandlerTest {

    @Mock
    private CardRepositoryPort cardRepository;

    @InjectMocks
    private UpdateCardCommandHandler handler;

    @Test
    void shouldUpdateCardSuccessfully() {
        // Given
        Card existingCard = newCardWithId(CARD_ID);
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.of(existingCard));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateCardCommand command = new UpdateCardCommand(
                CARD_ID,
                "Updated Front",
                "Updated Back",
                List.of("updated", "tags")
        );

        // When
        Card result = handler.handle(command);

        // Then
        assertNotNull(result);
        assertEquals("Updated Front", result.front());
        assertEquals("Updated Back", result.back());
        assertEquals(List.of("updated", "tags"), result.tags());
        verify(cardRepository).findById(CARD_ID);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void shouldThrowExceptionWhenCardNotFound() {
        // Given
        when(cardRepository.findById(999L)).thenReturn(Optional.empty());

        UpdateCardCommand command = new UpdateCardCommand(
                999L,
                "Front",
                "Back",
                List.of()
        );

        // When & Then
        assertThrows(CardNotFoundException.class, () -> handler.handle(command));
        verify(cardRepository).findById(999L);
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void shouldPreserveSchedulingDataWhenUpdatingContent() {
        // Given
        Card experiencedCardData = experiencedCard();
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.of(experiencedCardData));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateCardCommand command = new UpdateCardCommand(
                CARD_ID,
                "New Front",
                "New Back",
                List.of("new")
        );

        // When
        Card result = handler.handle(command);

        // Then
        assertEquals(experiencedCardData.easeFactor(), result.easeFactor());
        assertEquals(experiencedCardData.intervalDays(), result.intervalDays());
        assertEquals(experiencedCardData.repetitions(), result.repetitions());
        assertEquals(experiencedCardData.dueAt(), result.dueAt());
    }

    @Test
    void shouldAllowEmptyTags() {
        // Given
        Card existingCard = newCardWithId(CARD_ID);
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.of(existingCard));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateCardCommand command = new UpdateCardCommand(
                CARD_ID,
                "Front",
                "Back",
                List.of()
        );

        // When
        Card result = handler.handle(command);

        // Then
        assertTrue(result.tags().isEmpty());
    }
}
