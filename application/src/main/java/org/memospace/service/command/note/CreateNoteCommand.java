package org.memospace.service.command.note;

import org.memospace.service.Command;
import org.memospace.model.Note;

public record CreateNoteCommand(Note note) implements Command {
}