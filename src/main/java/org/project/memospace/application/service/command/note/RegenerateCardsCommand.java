package org.project.memospace.application.service.command.note;

import org.project.memospace.adapter.web.dto.RegenerateMode;
import org.project.memospace.application.service.Command;

import java.util.UUID;

public record RegenerateCardsCommand(UUID noteId, RegenerateMode mode) implements Command {
}