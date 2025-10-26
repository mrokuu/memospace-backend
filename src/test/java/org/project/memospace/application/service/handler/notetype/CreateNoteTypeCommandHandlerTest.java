package org.project.memospace.application.service.handler.notetype;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.memospace.application.service.command.notetype.CreateNoteTypeCommand;
import org.project.memospace.domain.model.NoteType;
import org.project.memospace.domain.port.NoteTypeRepositoryPort;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.project.memospace.util.TestFixtures.basicNoteType;

@ExtendWith(MockitoExtension.class)
class CreateNoteTypeCommandHandlerTest {

    @Mock
    private NoteTypeRepositoryPort noteTypeRepository;

    @InjectMocks
    private CreateNoteTypeCommandHandler handler;

    @Test
    void shouldCreateNoteTypeSuccessfully() {
        // Given
        NoteType noteType = basicNoteType();
        when(noteTypeRepository.existsByName("Basic")).thenReturn(false);
        when(noteTypeRepository.save(noteType)).thenReturn(noteType);

        CreateNoteTypeCommand command = new CreateNoteTypeCommand(noteType);

        // When
        NoteType result = handler.handle(command);

        // Then
        assertNotNull(result);
        verify(noteTypeRepository).existsByName("Basic");
        verify(noteTypeRepository).save(noteType);
    }

    @Test
    void shouldThrowExceptionWhenNoteTypeNameExists() {
        // Given
        NoteType noteType = basicNoteType();
        when(noteTypeRepository.existsByName("Basic")).thenReturn(true);

        CreateNoteTypeCommand command = new CreateNoteTypeCommand(noteType);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));
        verify(noteTypeRepository).existsByName("Basic");
        verify(noteTypeRepository, never()).save(any());
    }

    @Test
    void shouldCheckExistenceBeforeSaving() {
        // Given
        NoteType noteType = basicNoteType();
        when(noteTypeRepository.existsByName("Basic")).thenReturn(false);
        when(noteTypeRepository.save(noteType)).thenReturn(noteType);

        CreateNoteTypeCommand command = new CreateNoteTypeCommand(noteType);

        // When
        handler.handle(command);

        // Then
        var inOrder = inOrder(noteTypeRepository);
        inOrder.verify(noteTypeRepository).existsByName("Basic");
        inOrder.verify(noteTypeRepository).save(noteType);
    }
}
