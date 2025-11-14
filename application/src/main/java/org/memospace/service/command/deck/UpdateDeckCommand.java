package org.memospace.service.command.deck;

import org.memospace.service.Command;

public record UpdateDeckCommand(Long deckId, String name, String description) implements Command {
}