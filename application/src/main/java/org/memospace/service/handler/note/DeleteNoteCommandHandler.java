package org.memospace.service.handler.note;

import lombok.RequiredArgsConstructor;
import org.memospace.service.CommandHandler;
import org.memospace.service.command.note.DeleteNoteCommand;
import org.memospace.exception.NoteNotFoundException;
import org.memospace.port.CardRepositoryPort;
import org.memospace.port.NoteRepositoryPort;
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