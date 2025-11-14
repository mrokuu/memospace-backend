package org.memospace.service.command.deck;

import org.memospace.service.Command;

public record DeleteDeckCommand(Long deckId) implements Command {
}