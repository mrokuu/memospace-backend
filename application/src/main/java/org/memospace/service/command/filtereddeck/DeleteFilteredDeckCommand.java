package org.memospace.service.command.filtereddeck;

import org.memospace.service.Command;

import java.util.UUID;

public record DeleteFilteredDeckCommand(UUID filteredDeckId) implements Command {
}
