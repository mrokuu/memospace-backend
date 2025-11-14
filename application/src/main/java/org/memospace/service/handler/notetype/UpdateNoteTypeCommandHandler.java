package org.memospace.service.handler.notetype;

import lombok.RequiredArgsConstructor;
import org.memospace.service.CommandHandler;
import org.memospace.service.command.notetype.UpdateNoteTypeCommand;
import org.memospace.exception.NoteTypeNotFoundException;
import org.memospace.model.NoteType;
import org.memospace.port.NoteTypeRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class UpdateNoteTypeCommandHandler implements CommandHandler<UpdateNoteTypeCommand, NoteType> {

    private final NoteTypeRepositoryPort noteTypeRepository;

    @Override
    public NoteType handle(UpdateNoteTypeCommand command) {
        NoteType existingNoteType = noteTypeRepository.findById(command.noteTypeId())
                .orElseThrow(() -> new NoteTypeNotFoundException(command.noteTypeId()));

        if (noteTypeRepository.existsByNameAndIdNot(command.updatedNoteType().getName(), command.noteTypeId())) {
            throw new IllegalArgumentException("NoteType with name '" + command.updatedNoteType().getName() + "' already exists");
        }

        NoteType noteTypeToSave = existingNoteType.update(
                command.updatedNoteType().getName(),
                command.updatedNoteType().getFields(),
                command.updatedNoteType().getTemplates(),
                command.updatedNoteType().getCss()
        );

        return noteTypeRepository.save(noteTypeToSave);
    }
}