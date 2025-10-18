package org.project.memospace.application.service.handler.note;

import lombok.RequiredArgsConstructor;
import org.project.memospace.adapter.web.dto.RegenerateCardsDto;
import org.project.memospace.adapter.web.dto.RegenerateMode;
import org.project.memospace.application.service.CommandHandler;
import org.project.memospace.application.service.command.note.RegenerateCardsCommand;
import org.project.memospace.domain.exception.NoteNotFoundException;
import org.project.memospace.domain.exception.NoteTypeNotFoundException;
import org.project.memospace.domain.model.Card;
import org.project.memospace.domain.model.Note;
import org.project.memospace.domain.model.NoteType;
import org.project.memospace.domain.port.CardRepositoryPort;
import org.project.memospace.domain.port.NoteRepositoryPort;
import org.project.memospace.domain.port.NoteTypeRepositoryPort;
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