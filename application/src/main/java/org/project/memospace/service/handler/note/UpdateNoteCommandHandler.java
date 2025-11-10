package org.project.memospace.application.service.handler.note;

import lombok.RequiredArgsConstructor;
import org.project.memospace.dto.UpdateNoteDto;
import org.project.memospace.application.service.CommandHandler;
import org.project.memospace.application.service.command.note.UpdateNoteCommand;
import org.project.memospace.domain.exception.DeckNotFoundException;
import org.project.memospace.domain.exception.NoteNotFoundException;
import org.project.memospace.domain.exception.NoteTypeNotFoundException;
import org.project.memospace.domain.model.Card;
import org.project.memospace.domain.model.Note;
import org.project.memospace.domain.model.NoteType;
import org.project.memospace.domain.port.CardRepositoryPort;
import org.project.memospace.domain.port.DeckRepositoryPort;
import org.project.memospace.domain.port.NoteRepositoryPort;
import org.project.memospace.domain.port.NoteTypeRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
@Transactional
public class UpdateNoteCommandHandler implements CommandHandler<UpdateNoteCommand, UpdateNoteDto> {

    private final NoteRepositoryPort noteRepository;
    private final NoteTypeRepositoryPort noteTypeRepository;
    private final DeckRepositoryPort deckRepository;
    private final CardRepositoryPort cardRepository;
    private final NoteCardGenerationService cardGenerationService;

    @Override
    public UpdateNoteDto handle(UpdateNoteCommand command) {
        // Get existing note
        Note existingNote = noteRepository.findById(command.noteId())
                .orElseThrow(() -> new NoteNotFoundException(command.noteId()));

        // Validate deck exists if deckId is provided
        if (command.deckId() != null && !deckRepository.existsById(command.deckId())) {
            throw new DeckNotFoundException(command.deckId());
        }

        // Get NoteType
        NoteType noteType = noteTypeRepository.findById(existingNote.noteTypeId())
                .orElseThrow(() -> new NoteTypeNotFoundException(existingNote.noteTypeId()));

        // Use existing values if not provided
        Long finalDeckId = command.deckId() != null ? command.deckId() : existingNote.deckId();
        Map<String, String> finalFieldValues = command.fieldValues() != null ?
                new HashMap<>(command.fieldValues()) : new HashMap<>(existingNote.fieldValues());
        Set<String> finalTags = command.tags() != null ?
                StreamSupport.stream(command.tags().spliterator(), false).collect(Collectors.toSet()) : existingNote.tags();

        // Ensure all declared fields exist in fieldValues (set missing to empty string)
        for (String field : noteType.getFields()) {
            finalFieldValues.putIfAbsent(field, "");
        }

        // Update note
        Note updatedNote = existingNote.update(finalDeckId, finalFieldValues, finalTags);
        Note savedNote = noteRepository.save(updatedNote);

        // Regenerate cards in REPLACE mode
        cardRepository.deleteAllByNoteId(command.noteId());
        List<Card> generatedCards = cardGenerationService.generateCardsForNote(savedNote, noteType);
        List<Card> savedCards = cardRepository.saveAll(generatedCards);

        return new UpdateNoteDto(savedNote, savedCards.size());
    }
}