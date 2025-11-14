package org.memospace.service.handler.note;

import lombok.RequiredArgsConstructor;
import org.memospace.dto.UpdateNoteDto;
import org.memospace.service.CommandHandler;
import org.memospace.service.command.note.UpdateNoteCommand;
import org.memospace.exception.DeckNotFoundException;
import org.memospace.exception.NoteNotFoundException;
import org.memospace.exception.NoteTypeNotFoundException;
import org.memospace.model.Card;
import org.memospace.model.Note;
import org.memospace.model.NoteType;
import org.memospace.port.CardRepositoryPort;
import org.memospace.port.DeckRepositoryPort;
import org.memospace.port.NoteRepositoryPort;
import org.memospace.port.NoteTypeRepositoryPort;
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