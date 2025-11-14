package org.memospace.service.command.notetype;

import org.memospace.service.Command;
import org.memospace.model.NoteType;

import java.util.UUID;

public record UpdateNoteTypeCommand(UUID noteTypeId, NoteType updatedNoteType) implements Command {
}