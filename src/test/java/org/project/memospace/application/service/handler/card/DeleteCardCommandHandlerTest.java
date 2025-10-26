package org.project.memospace.application.service.handler.card;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.memospace.application.service.command.card.DeleteCardCommand;
import org.project.memospace.domain.exception.CardNotFoundException;
import org.project.memospace.domain.port.CardRepositoryPort;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.project.memospace.util.TestFixtures.CARD_ID;

@ExtendWith(MockitoExtension.class)
class DeleteCardCommandHandlerTest {

    @Mock
    private CardRepositoryPort cardRepository;

    @InjectMocks
    private DeleteCardCommandHandler handler;

    @Test
    void shouldDeleteCardSuccessfully() {
        // Given
        when(cardRepository.existsById(CARD_ID)).thenReturn(true);
        DeleteCardCommand command = new DeleteCardCommand(CARD_ID);

        // When
        Void result = handler.handle(command);

        // Then
        assertNull(result);
        verify(cardRepository).existsById(CARD_ID);
        verify(cardRepository).deleteById(CARD_ID);
    }

    @Test
    void shouldThrowExceptionWhenCardNotFound() {
        // Given
        when(cardRepository.existsById(999L)).thenReturn(false);
        DeleteCardCommand command = new DeleteCardCommand(999L);

        // When & Then
        assertThrows(CardNotFoundException.class, () -> handler.handle(command));
        verify(cardRepository).existsById(999L);
        verify(cardRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldCheckExistenceBeforeDeleting() {
        // Given
        when(cardRepository.existsById(CARD_ID)).thenReturn(true);
        DeleteCardCommand command = new DeleteCardCommand(CARD_ID);

        // When
        handler.handle(command);

        // Then
        var inOrder = inOrder(cardRepository);
        inOrder.verify(cardRepository).existsById(CARD_ID);
        inOrder.verify(cardRepository).deleteById(CARD_ID);
    }
}
