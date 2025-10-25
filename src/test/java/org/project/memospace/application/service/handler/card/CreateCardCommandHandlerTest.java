package org.project.memospace.application.service.handler.card;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.memospace.application.service.command.card.CreateCardCommand;
import org.project.memospace.domain.exception.DeckNotFoundException;
import org.project.memospace.domain.model.Card;
import org.project.memospace.domain.port.CardRepositoryPort;
import org.project.memospace.domain.port.DeckRepositoryPort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.project.memospace.util.TestFixtures.*;

@ExtendWith(MockitoExtension.class)
class CreateCardCommandHandlerTest {

    @Mock
    private CardRepositoryPort cardRepository;

    @Mock
    private DeckRepositoryPort deckRepository;

    @InjectMocks
    private CreateCardCommandHandler handler;

    @BeforeEach
    void setUp() {
        // Default: deck exists
        when(deckRepository.existsById(anyLong())).thenReturn(true);
    }

    @Test
    void shouldCreateCardSuccessfully() {
        // Given
        CreateCardCommand command = new CreateCardCommand(
                DECK_ID,
                "What is Java?",
                "A programming language",
                List.of("programming", "java")
        );

        Card expectedCard = newCard();
        when(cardRepository.save(any(Card.class))).thenReturn(expectedCard);

        // When
        Card result = handler.handle(command);

        // Then
        assertNotNull(result);
        verify(deckRepository).existsById(DECK_ID);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void shouldCreateCardWithEmptyTags() {
        // Given
        CreateCardCommand command = new CreateCardCommand(
                DECK_ID,
                "Front",
                "Back",
                List.of()
        );

        Card savedCard = newCardWithId(CARD_ID);
        when(cardRepository.save(any(Card.class))).thenReturn(savedCard);

        // When
        Card result = handler.handle(command);

        // Then
        assertNotNull(result);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void shouldThrowExceptionWhenDeckNotFound() {
        // Given
        CreateCardCommand command = new CreateCardCommand(
                999L,
                "Front",
                "Back",
                List.of()
        );

        when(deckRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(DeckNotFoundException.class, () -> handler.handle(command));
        verify(deckRepository).existsById(999L);
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void shouldPassCorrectDataToRepository() {
        // Given
        String expectedFront = "Test Front";
        String expectedBack = "Test Back";
        List<String> expectedTags = List.of("tag1", "tag2");

        CreateCardCommand command = new CreateCardCommand(
                DECK_ID,
                expectedFront,
                expectedBack,
                expectedTags
        );

        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Card result = handler.handle(command);

        // Then
        assertEquals(expectedFront, result.front());
        assertEquals(expectedBack, result.back());
        assertEquals(expectedTags, result.tags());
        assertEquals(DECK_ID, result.deckId());
    }

    @Test
    void shouldSetDefaultSchedulingForNewCard() {
        // Given
        CreateCardCommand command = new CreateCardCommand(
                DECK_ID,
                "Front",
                "Back",
                List.of()
        );

        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Card result = handler.handle(command);

        // Then
        assertEquals(2.5, result.easeFactor());
        assertEquals(1, result.intervalDays());
        assertEquals(0, result.repetitions());
        assertNotNull(result.dueAt());
    }
}
