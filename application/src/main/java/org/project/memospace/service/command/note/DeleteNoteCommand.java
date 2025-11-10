package org.project.memospace.application.service.command.note;

import org.project.memospace.application.service.Command;

import java.util.UUID;

public record DeleteNoteCommand(UUID noteId) implements Command {
}