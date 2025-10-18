package org.project.memospace.application.service.handler.note;

import lombok.RequiredArgsConstructor;
import org.project.memospace.application.service.CommandHandler;
import org.project.memospace.application.service.command.note.DeleteNoteCommand;
import org.project.memospace.domain.exception.NoteNotFoundException;
import org.project.memospace.domain.port.CardRepositoryPort;
import org.project.memospace.domain.port.NoteRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class DeleteNoteCommandHandler implements CommandHandler<DeleteNoteCommand, Void> {

    private final NoteRepositoryPort noteRepository;
    private final CardRepositoryPort cardRepository;

    @Override
    public Void handle(DeleteNoteCommand command) {

        if (!noteRepository.existsById(command.noteId())) {
            throw new NoteNotFoundException(command.noteId());
        }

        // Delete associated cards first
        cardRepository.deleteAllByNoteId(command.noteId());

        // Delete note
        noteRepository.deleteById(command.noteId());
        return null;
    }
}