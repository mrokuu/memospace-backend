package org.memospace.service.handler.note;

import lombok.RequiredArgsConstructor;
import org.memospace.dto.CreateNoteDto;
import org.memospace.service.CommandHandler;
import org.memospace.service.command.note.CreateNoteCommand;
import org.memospace.exception.DeckNotFoundException;
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

@Component
@RequiredArgsConstructor
@Transactional
public class CreateNoteCommandHandler implements CommandHandler<CreateNoteCommand, CreateNoteDto> {

    private final NoteRepositoryPort noteRepository;
    private final NoteTypeRepositoryPort noteTypeRepository;
    private final DeckRepositoryPort deckRepository;
    private final CardRepositoryPort cardRepository;
    private final NoteCardGenerationService cardGenerationService;

    @Override
    public CreateNoteDto handle(CreateNoteCommand command) {
        // Validate deck exists
        if (!deckRepository.existsById(command.note().deckId())) {
            throw new DeckNotFoundException(command.note().deckId());
        }

        // Get NoteType and validate it exists
        NoteType noteType = noteTypeRepository.findById(command.note().noteTypeId())
                .orElseThrow(() -> new NoteTypeNotFoundException(command.note().noteTypeId()));

        // Ensure all declared fields exist in fieldValues (set missing to empty string)
        Map<String, String> completeFieldValues = new HashMap<>(command.note().fieldValues());
        for (String field : noteType.getFields()) {
            completeFieldValues.putIfAbsent(field, "");
        }

        Note noteToSave = Note.create(
                command.note().deckId(),
                command.note().noteTypeId(),
                completeFieldValues,
                command.note().tags()
        );

        // Save note
        Note savedNote = noteRepository.save(noteToSave);

        // Generate and save cards
        List<Card> generatedCards = cardGenerationService.generateCardsForNote(savedNote, noteType);
        List<Card> savedCards = cardRepository.saveAll(generatedCards);

        return new CreateNoteDto(savedNote, savedCards.size());
    }
}