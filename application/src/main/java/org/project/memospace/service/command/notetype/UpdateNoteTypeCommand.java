package org.project.memospace.application.service.command.notetype;

import org.project.memospace.application.service.Command;
import org.project.memospace.domain.model.NoteType;

import java.util.UUID;

public record UpdateNoteTypeCommand(UUID noteTypeId, NoteType updatedNoteType) implements Command {
}