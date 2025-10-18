package org.project.memospace.application.service.command.deck;

import org.project.memospace.application.service.Command;

public record DeleteDeckCommand(Long deckId) implements Command {
}