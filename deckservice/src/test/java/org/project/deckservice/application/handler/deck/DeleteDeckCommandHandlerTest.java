package org.project.deckservice.application.handler.deck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.deckservice.adapter.persistance.repository.DeckRepositoryAdapter;
import org.project.deckservice.application.command.DeleteDeckCommand;
import org.project.deckservice.domain.exception.DeckNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteDeckCommandHandler Unit Tests")
class DeleteDeckCommandHandlerTest {

    @Mock
    private DeckRepositoryAdapter deckRepositoryAdapter;

    @InjectMocks
    private DeleteDeckCommandHandler handler;

    private DeleteDeckCommand command;

    @BeforeEach
    void setUp() {
        command = new DeleteDeckCommand(1L);
    }

    @Test
    @DisplayName("should delete deck successfully when deck exists")
    void shouldDeleteDeck_WhenDeckExists() {
        // given
        when(deckRepositoryAdapter.existsById(1L)).thenReturn(true);
        doNothing().when(deckRepositoryAdapter).deleteById(1L);

        // when
        Void result = handler.handle(command);

        // then
        assertThat(result).isNull();
        verify(deckRepositoryAdapter).existsById(1L);
        verify(deckRepositoryAdapter).deleteById(1L);
    }

    @Test
    @DisplayName("should throw DeckNotFoundException when deck does not exist")
    void shouldThrowException_WhenDeckNotFound() {
        // given
        when(deckRepositoryAdapter.existsById(1L)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> handler.handle(command))
            .isInstanceOf(DeckNotFoundException.class)
            .hasMessageContaining("Deck not found with id: 1");

        verify(deckRepositoryAdapter).existsById(1L);
        verify(deckRepositoryAdapter, never()).deleteById(any());
    }

    @Test
    @DisplayName("should check existence before deleting")
    void shouldCheckExistence_BeforeDeleting() {
        // given
        when(deckRepositoryAdapter.existsById(1L)).thenReturn(true);
        doNothing().when(deckRepositoryAdapter).deleteById(1L);

        // when
        handler.handle(command);

        // then
        var inOrder = inOrder(deckRepositoryAdapter);
        inOrder.verify(deckRepositoryAdapter).existsById(1L);
        inOrder.verify(deckRepositoryAdapter).deleteById(1L);
    }

    @Test
    @DisplayName("should delete deck with specific ID")
    void shouldDeleteDeck_WithSpecificId() {
        // given
        Long deckId = 42L;
        DeleteDeckCommand cmd = new DeleteDeckCommand(deckId);
        when(deckRepositoryAdapter.existsById(deckId)).thenReturn(true);
        doNothing().when(deckRepositoryAdapter).deleteById(deckId);

        // when
        handler.handle(cmd);

        // then
        verify(deckRepositoryAdapter).existsById(eq(42L));
        verify(deckRepositoryAdapter).deleteById(eq(42L));
    }

    @Test
    @DisplayName("should throw exception for non-existent deck ID")
    void shouldThrowException_WhenDeckIdDoesNotExist() {
        // given
        Long nonExistentId = 999L;
        DeleteDeckCommand cmdWithBadId = new DeleteDeckCommand(nonExistentId);
        when(deckRepositoryAdapter.existsById(nonExistentId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> handler.handle(cmdWithBadId))
            .isInstanceOf(DeckNotFoundException.class)
            .hasMessageContaining("Deck not found with id: 999");

        verify(deckRepositoryAdapter).existsById(nonExistentId);
        verify(deckRepositoryAdapter, never()).deleteById(any());
    }

    @Test
    @DisplayName("should return null when deletion succeeds")
    void shouldReturnNull_WhenDeletionSucceeds() {
        // given
        when(deckRepositoryAdapter.existsById(1L)).thenReturn(true);
        doNothing().when(deckRepositoryAdapter).deleteById(1L);

        // when
        Void result = handler.handle(command);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("should invoke deleteById exactly once when deck exists")
    void shouldInvokeDeleteByIdOnce_WhenDeckExists() {
        // given
        when(deckRepositoryAdapter.existsById(1L)).thenReturn(true);
        doNothing().when(deckRepositoryAdapter).deleteById(1L);

        // when
        handler.handle(command);

        // then
        verify(deckRepositoryAdapter, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("should invoke existsById exactly once")
    void shouldInvokeExistsByIdOnce_WhenHandlingCommand() {
        // given
        when(deckRepositoryAdapter.existsById(1L)).thenReturn(true);
        doNothing().when(deckRepositoryAdapter).deleteById(1L);

        // when
        handler.handle(command);

        // then
        verify(deckRepositoryAdapter, times(1)).existsById(1L);
    }

    @Test
    @DisplayName("should not call deleteById when deck does not exist")
    void shouldNotCallDeleteById_WhenDeckDoesNotExist() {
        // given
        when(deckRepositoryAdapter.existsById(1L)).thenReturn(false);

        // when & then
        try {
            handler.handle(command);
        } catch (DeckNotFoundException e) {
            // expected
        }

        verify(deckRepositoryAdapter, never()).deleteById(any());
    }

    @Test
    @DisplayName("should handle deletion of deck with zero ID")
    void shouldHandleDeletion_WhenDeckIdIsZero() {
        // given
        Long zeroId = 0L;
        DeleteDeckCommand cmdWithZeroId = new DeleteDeckCommand(zeroId);
        when(deckRepositoryAdapter.existsById(zeroId)).thenReturn(true);
        doNothing().when(deckRepositoryAdapter).deleteById(zeroId);

        // when
        Void result = handler.handle(cmdWithZeroId);

        // then
        assertThat(result).isNull();
        verify(deckRepositoryAdapter).existsById(zeroId);
        verify(deckRepositoryAdapter).deleteById(zeroId);
    }
}
