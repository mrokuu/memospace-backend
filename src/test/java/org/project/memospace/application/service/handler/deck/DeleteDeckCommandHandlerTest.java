package org.project.memospace.application.service.handler.deck;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.memospace.application.service.command.deck.DeleteDeckCommand;
import org.project.memospace.domain.exception.DeckNotFoundException;
import org.project.memospace.domain.port.DeckRepositoryPort;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.project.memospace.util.TestFixtures.DECK_ID;

@ExtendWith(MockitoExtension.class)
class DeleteDeckCommandHandlerTest {

    @Mock
    private DeckRepositoryPort deckRepository;

    @InjectMocks
    private DeleteDeckCommandHandler handler;

    @Test
    void shouldDeleteDeckSuccessfully() {
        // Given
        when(deckRepository.existsById(DECK_ID)).thenReturn(true);
        DeleteDeckCommand command = new DeleteDeckCommand(DECK_ID);

        // When
        Void result = handler.handle(command);

        // Then
        assertNull(result);
        verify(deckRepository).existsById(DECK_ID);
        verify(deckRepository).deleteById(DECK_ID);
    }

    @Test
    void shouldThrowExceptionWhenDeckNotFound() {
        // Given
        when(deckRepository.existsById(999L)).thenReturn(false);
        DeleteDeckCommand command = new DeleteDeckCommand(999L);

        // When & Then
        assertThrows(DeckNotFoundException.class, () -> handler.handle(command));
        verify(deckRepository).existsById(999L);
        verify(deckRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldCheckExistenceBeforeDeleting() {
        // Given
        when(deckRepository.existsById(DECK_ID)).thenReturn(true);
        DeleteDeckCommand command = new DeleteDeckCommand(DECK_ID);

        // When
        handler.handle(command);

        // Then
        var inOrder = inOrder(deckRepository);
        inOrder.verify(deckRepository).existsById(DECK_ID);
        inOrder.verify(deckRepository).deleteById(DECK_ID);
    }
}
