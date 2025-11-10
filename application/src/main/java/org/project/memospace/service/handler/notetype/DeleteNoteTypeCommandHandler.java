package org.project.memospace.application.service.handler.notetype;

import lombok.RequiredArgsConstructor;
import org.project.memospace.application.service.CommandHandler;
import org.project.memospace.application.service.command.notetype.DeleteNoteTypeCommand;
import org.project.memospace.domain.exception.NoteTypeNotFoundException;
import org.project.memospace.domain.port.NoteRepositoryPort;
import org.project.memospace.domain.port.NoteTypeRepositoryPort;
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