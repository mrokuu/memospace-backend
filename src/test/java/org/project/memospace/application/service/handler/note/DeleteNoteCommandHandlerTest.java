package org.project.memospace.application.service.handler.note;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.memospace.application.service.command.note.DeleteNoteCommand;
import org.project.memospace.domain.exception.NoteNotFoundException;
import org.project.memospace.domain.port.CardRepositoryPort;
import org.project.memospace.domain.port.NoteRepositoryPort;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.project.memospace.util.TestFixtures.FIXED_UUID_1;

@ExtendWith(MockitoExtension.class)
class DeleteNoteCommandHandlerTest {

    @Mock
    private NoteRepositoryPort noteRepository;

    @Mock
    private CardRepositoryPort cardRepository;

    @InjectMocks
    private DeleteNoteCommandHandler handler;

    @Test
    void shouldDeleteNoteAndAssociatedCardsSuccessfully() {
        // Given
        when(noteRepository.existsById(FIXED_UUID_1)).thenReturn(true);
        DeleteNoteCommand command = new DeleteNoteCommand(FIXED_UUID_1);

        // When
        Void result = handler.handle(command);

        // Then
        assertNull(result);
        verify(noteRepository).existsById(FIXED_UUID_1);
        verify(cardRepository).deleteAllByNoteId(FIXED_UUID_1);
        verify(noteRepository).deleteById(FIXED_UUID_1);
    }

    @Test
    void shouldThrowExceptionWhenNoteNotFound() {
        // Given
        when(noteRepository.existsById(FIXED_UUID_1)).thenReturn(false);
        DeleteNoteCommand command = new DeleteNoteCommand(FIXED_UUID_1);

        // When & Then
        assertThrows(NoteNotFoundException.class, () -> handler.handle(command));
        verify(noteRepository).existsById(FIXED_UUID_1);
        verify(cardRepository, never()).deleteAllByNoteId(any());
        verify(noteRepository, never()).deleteById(any());
    }

    @Test
    void shouldDeleteCardsBeforeNote() {
        // Given
        when(noteRepository.existsById(FIXED_UUID_1)).thenReturn(true);
        DeleteNoteCommand command = new DeleteNoteCommand(FIXED_UUID_1);

        // When
        handler.handle(command);

        // Then
        var inOrder = inOrder(cardRepository, noteRepository);
        inOrder.verify(cardRepository).deleteAllByNoteId(FIXED_UUID_1);
        inOrder.verify(noteRepository).deleteById(FIXED_UUID_1);
    }
}
