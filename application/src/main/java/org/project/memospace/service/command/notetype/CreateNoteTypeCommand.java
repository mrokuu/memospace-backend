package org.project.memospace.application.service.command.notetype;

import org.project.memospace.application.service.Command;
import org.project.memospace.domain.model.NoteType;

public record CreateNoteTypeCommand(NoteType noteType) implements Command {
}