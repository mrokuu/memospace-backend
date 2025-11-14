package org.memospace.service.command.notetype;

import org.memospace.service.Command;
import org.memospace.model.NoteType;

public record CreateNoteTypeCommand(NoteType noteType) implements Command {
}