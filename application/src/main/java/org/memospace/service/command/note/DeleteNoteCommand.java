package org.memospace.service.command.note;

import org.memospace.service.Command;

import java.util.UUID;

public record DeleteNoteCommand(UUID noteId) implements Command {
}