package org.memospace.service.handler.notetype;

import lombok.RequiredArgsConstructor;
import org.memospace.service.CommandHandler;
import org.memospace.service.command.notetype.CreateNoteTypeCommand;
import org.memospace.model.NoteType;
import org.memospace.port.NoteTypeRepositoryPort;
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