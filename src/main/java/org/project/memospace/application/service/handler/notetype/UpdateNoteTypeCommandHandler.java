package org.project.memospace.application.service.handler.notetype;

import lombok.RequiredArgsConstructor;
import org.project.memospace.application.service.CommandHandler;
import org.project.memospace.application.service.command.notetype.UpdateNoteTypeCommand;
import org.project.memospace.domain.exception.NoteTypeNotFoundException;
import org.project.memospace.domain.model.NoteType;
import org.project.memospace.domain.port.NoteTypeRepositoryPort;
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