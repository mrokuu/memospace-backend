package org.project.memospace.application.service.command.notetype;

import org.project.memospace.application.service.Command;

import java.util.UUID;

public record DeleteNoteTypeCommand(UUID noteTypeId) implements Command {
}