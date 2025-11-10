package org.project.memospace.application.service.command.filtereddeck;

import org.project.memospace.application.service.Command;

import java.util.UUID;

public record RebuildFilteredDeckCommand(UUID filteredDeckId) implements Command {
}
