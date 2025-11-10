package org.project.memospace.application.service.handler.notetype;

import lombok.RequiredArgsConstructor;
import org.project.memospace.application.service.CommandHandler;
import org.project.memospace.application.service.command.notetype.CreateNoteTypeCommand;
import org.project.memospace.domain.model.NoteType;
import org.project.memospace.domain.port.NoteTypeRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class CreateNoteTypeCommandHandler implements CommandHandler<CreateNoteTypeCommand, NoteType> {

    private final NoteTypeRepositoryPort noteTypeRepository;

    @Override
    public NoteType handle(CreateNoteTypeCommand command) {
        if (noteTypeRepository.existsByName(command.noteType().getName())) {
            throw new IllegalArgumentException("NoteType with name '" + command.noteType().getName() + "' already exists");
        }

        return noteTypeRepository.save(command.noteType());
    }
}