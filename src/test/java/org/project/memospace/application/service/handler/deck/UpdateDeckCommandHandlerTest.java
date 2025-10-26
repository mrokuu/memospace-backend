package org.project.memospace.application.service.handler.deck;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.memospace.application.service.command.deck.UpdateDeckCommand;
import org.project.memospace.domain.exception.DeckNotFoundException;
import org.project.memospace.domain.model.Deck;
import org.project.memospace.domain.port.DeckRepositoryPort;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.project.memospace.util.TestFixtures.*;

@ExtendWith(MockitoExtension.class)
class UpdateDeckCommandHandlerTest {

    @Mock
    private DeckRepositoryPort deckRepository;

    @InjectMocks
    private UpdateDeckCommandHandler handler;

    @Test
    void shouldUpdateDeckSuccessfully() {
        // Given
        Deck existingDeck = defaultDeck();
        when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(existingDeck));
        when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateDeckCommand command = new UpdateDeckCommand(DECK_ID, "Updated Name", "Updated Description");

        // When
        Deck result = handler.handle(command);

        // Then
        assertNotNull(result);
        assertEquals("Updated Name", result.name());
        assertEquals("Updated Description", result.description());
        verify(deckRepository).findById(DECK_ID);
        verify(deckRepository).save(any(Deck.class));
    }

    @Test
    void shouldThrowExceptionWhenDeckNotFound() {
        // Given
        when(deckRepository.findById(999L)).thenReturn(Optional.empty());
        UpdateDeckCommand command = new UpdateDeckCommand(999L, "Name", "Description");

        // When & Then
        assertThrows(DeckNotFoundException.class, () -> handler.handle(command));
        verify(deckRepository).findById(999L);
        verify(deckRepository, never()).save(any(Deck.class));
    }

    @Test
    void shouldPreserveTimestampsWhenUpdating() {
        // Given
        Deck existingDeck = defaultDeck();
        when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(existingDeck));
        when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateDeckCommand command = new UpdateDeckCommand(DECK_ID, "New Name", "New Description");

        // When
        Deck result = handler.handle(command);

        // Then
        assertEquals(existingDeck.createdAt(), result.createdAt());
    }

    @Test
    void shouldAllowNullDescription() {
        // Given
        Deck existingDeck = defaultDeck();
        when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(existingDeck));
        when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateDeckCommand command = new UpdateDeckCommand(DECK_ID, "Name", null);

        // When
        Deck result = handler.handle(command);

        // Then
        assertEquals("Name", result.name());
        assertNull(result.description());
    }
}
