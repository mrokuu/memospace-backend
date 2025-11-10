package org.project.memospace.application.service.command.note;

import org.project.memospace.application.service.Command;
import org.project.memospace.domain.model.Note;

public record CreateNoteCommand(Note note) implements Command {
}