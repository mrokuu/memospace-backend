package org.memospace.service.handler.notetype;

import lombok.RequiredArgsConstructor;
import org.memospace.service.CommandHandler;
import org.memospace.service.command.notetype.DeleteNoteTypeCommand;
import org.memospace.exception.NoteTypeNotFoundException;
import org.memospace.port.NoteRepositoryPort;
import org.memospace.port.NoteTypeRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class DeleteNoteTypeCommandHandler implements CommandHandler<DeleteNoteTypeCommand, Void> {

    private final NoteTypeRepositoryPort noteTypeRepository;
    private final NoteRepositoryPort noteRepository;

    @Override
    public Void handle(DeleteNoteTypeCommand command) {

        if (!noteTypeRepository.findById(command.noteTypeId()).isPresent()) {
            throw new NoteTypeNotFoundException(command.noteTypeId());
        }

        // Check if NoteType is in use
        if (!noteRepository.findByNoteTypeId(command.noteTypeId()).isEmpty()) {
            throw new IllegalStateException("Cannot delete NoteType that is in use by notes");
        }

        noteTypeRepository.deleteById(command.noteTypeId());
        return null;
    }
}