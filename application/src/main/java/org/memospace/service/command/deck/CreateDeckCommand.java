package org.memospace.service.command.deck;

import org.memospace.service.Command;

public record CreateDeckCommand(String name, String description) implements Command {
}