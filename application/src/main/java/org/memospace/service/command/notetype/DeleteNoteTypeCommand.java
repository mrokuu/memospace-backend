package org.memospace.service.command.notetype;

import org.memospace.service.Command;

import java.util.UUID;

public record DeleteNoteTypeCommand(UUID noteTypeId) implements Command {
}