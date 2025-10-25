package org.project.memospace.application.service.handler.note;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.memospace.adapter.web.dto.CreateNoteDto;
import org.project.memospace.application.service.command.note.CreateNoteCommand;
import org.project.memospace.domain.exception.DeckNotFoundException;
import org.project.memospace.domain.exception.NoteTypeNotFoundException;
import org.project.memospace.domain.model.Card;
import org.project.memospace.domain.model.Note;
import org.project.memospace.domain.model.NoteType;
import org.project.memospace.domain.port.CardRepositoryPort;
import org.project.memospace.domain.port.DeckRepositoryPort;
import org.project.memospace.domain.port.NoteRepositoryPort;
import org.project.memospace.domain.port.NoteTypeRepositoryPort;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.project.memospace.util.TestFixtures.*;

@ExtendWith(MockitoExtension.class)
class CreateNoteCommandHandlerTest {

    @Mock
    private NoteRepositoryPort noteRepository;

    @Mock
    private NoteTypeRepositoryPort noteTypeRepository;

    @Mock
    private DeckRepositoryPort deckRepository;

    @Mock
    private CardRepositoryPort cardRepository;

    @Mock
    private NoteCardGenerationService cardGenerationService;

    @InjectMocks
    private CreateNoteCommandHandler handler;

    @Test
    void shouldCreateNoteWithCardsSuccessfully() {
        // Given
        NoteType noteType = basicNoteType();
        Note savedNote = basicNote();
        List<Card> generatedCards = List.of(newCard(), newCardWithId(201L));

        when(deckRepository.existsById(DECK_ID)).thenReturn(true);
        when(noteTypeRepository.findById(FIXED_UUID_2)).thenReturn(Optional.of(noteType));
        when(noteRepository.save(any(Note.class))).thenReturn(savedNote);
        when(cardGenerationService.generateCardsForNote(savedNote, noteType)).thenReturn(generatedCards);
        when(cardRepository.saveAll(anyList())).thenReturn(generatedCards);

        Note note = Note.create(DECK_ID, FIXED_UUID_2, Map.of("Front", "Q", "Back", "A"), Set.of("test"));
        CreateNoteCommand command = new CreateNoteCommand(note);

        // When
        CreateNoteDto result = handler.handle(command);

        // Then
        assertNotNull(result);
        assertEquals(2, result.cardCount());
        verify(deckRepository).existsById(DECK_ID);
        verify(noteTypeRepository).findById(FIXED_UUID_2);
        verify(noteRepository).save(any(Note.class));
        verify(cardGenerationService).generateCardsForNote(savedNote, noteType);
        verify(cardRepository).saveAll(anyList());
    }

    @Test
    void shouldThrowExceptionWhenDeckNotFound() {
        // Given
        when(deckRepository.existsById(999L)).thenReturn(false);

        Note note = Note.create(999L, FIXED_UUID_2, Map.of("Front", "Q"), Set.of());
        CreateNoteCommand command = new CreateNoteCommand(note);

        // When & Then
        assertThrows(DeckNotFoundException.class, () -> handler.handle(command));
        verify(deckRepository).existsById(999L);
        verifyNoInteractions(noteRepository, cardRepository, cardGenerationService);
    }

    @Test
    void shouldThrowExceptionWhenNoteTypeNotFound() {
        // Given
        when(deckRepository.existsById(DECK_ID)).thenReturn(true);
        when(noteTypeRepository.findById(FIXED_UUID_2)).thenReturn(Optional.empty());

        Note note = Note.create(DECK_ID, FIXED_UUID_2, Map.of("Front", "Q"), Set.of());
        CreateNoteCommand command = new CreateNoteCommand(note);

        // When & Then
        assertThrows(NoteTypeNotFoundException.class, () -> handler.handle(command));
        verify(noteTypeRepository).findById(FIXED_UUID_2);
        verifyNoInteractions(cardRepository, cardGenerationService);
    }

    @Test
    void shouldFillMissingFieldsWithEmptyStrings() {
        // Given
        NoteType noteType = basicNoteType(); // Has fields: Front, Back
        Note savedNote = basicNote();

        when(deckRepository.existsById(DECK_ID)).thenReturn(true);
        when(noteTypeRepository.findById(FIXED_UUID_2)).thenReturn(Optional.of(noteType));
        when(noteRepository.save(any(Note.class))).thenReturn(savedNote);
        when(cardGenerationService.generateCardsForNote(any(), any())).thenReturn(List.of());
        when(cardRepository.saveAll(anyList())).thenReturn(List.of());

        // Only provide "Front" field, "Back" should be filled with empty string
        Note note = Note.create(DECK_ID, FIXED_UUID_2, Map.of("Front", "Question only"), Set.of());
        CreateNoteCommand command = new CreateNoteCommand(note);

        // When
        handler.handle(command);

        // Then
        verify(noteRepository).save(any(Note.class));
    }

    @Test
    void shouldGenerateCorrectNumberOfCards() {
        // Given
        NoteType noteType = basicNoteType();
        Note savedNote = basicNote();
        List<Card> generatedCards = List.of(newCard());

        when(deckRepository.existsById(DECK_ID)).thenReturn(true);
        when(noteTypeRepository.findById(FIXED_UUID_2)).thenReturn(Optional.of(noteType));
        when(noteRepository.save(any(Note.class))).thenReturn(savedNote);
        when(cardGenerationService.generateCardsForNote(savedNote, noteType)).thenReturn(generatedCards);
        when(cardRepository.saveAll(anyList())).thenReturn(generatedCards);

        Note note = Note.create(DECK_ID, FIXED_UUID_2, Map.of("Front", "Q", "Back", "A"), Set.of());
        CreateNoteCommand command = new CreateNoteCommand(note);

        // When
        CreateNoteDto result = handler.handle(command);

        // Then
        assertEquals(1, result.cardCount());
    }
}
