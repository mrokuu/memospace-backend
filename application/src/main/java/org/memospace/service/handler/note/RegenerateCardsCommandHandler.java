package org.memospace.service.handler.note;

import lombok.RequiredArgsConstructor;
import org.memospace.dto.RegenerateCardsDto;
import org.memospace.dto.RegenerateMode;
import org.memospace.service.CommandHandler;
import org.memospace.service.command.note.RegenerateCardsCommand;
import org.memospace.exception.NoteNotFoundException;
import org.memospace.exception.NoteTypeNotFoundException;
import org.memospace.model.Card;
import org.memospace.model.Note;
import org.memospace.model.NoteType;
import org.memospace.port.CardRepositoryPort;
import org.memospace.port.NoteRepositoryPort;
import org.memospace.port.NoteTypeRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class RegenerateCardsCommandHandler implements CommandHandler<RegenerateCardsCommand, RegenerateCardsDto> {

    private final NoteRepositoryPort noteRepository;
    private final NoteTypeRepositoryPort noteTypeRepository;
    private final CardRepositoryPort cardRepository;
    private final NoteCardGenerationService cardGenerationService;

    @Override
    public RegenerateCardsDto handle(RegenerateCardsCommand command) {

        Note note = noteRepository.findById(command.noteId())
                .orElseThrow(() -> new NoteNotFoundException(command.noteId()));

        NoteType noteType = noteTypeRepository.findById(note.noteTypeId())
                .orElseThrow(() -> new NoteTypeNotFoundException(note.noteTypeId()));

        List<Card> generatedCards = cardGenerationService.generateCardsForNote(note, noteType);

        if (command.mode() == RegenerateMode.REPLACE) {
            cardRepository.deleteAllByNoteId(note.noteTypeId());
        }

        List<Card> savedCards = cardRepository.saveAll(generatedCards);

        return new RegenerateCardsDto(savedCards.size(), command.mode());
    }
}