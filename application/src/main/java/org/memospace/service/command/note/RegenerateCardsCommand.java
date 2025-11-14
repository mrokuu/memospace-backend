package org.memospace.service.command.note;

import org.memospace.dto.RegenerateMode;
import org.memospace.service.Command;

import java.util.UUID;

public record RegenerateCardsCommand(UUID noteId, RegenerateMode mode) implements Command {
}