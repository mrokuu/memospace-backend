package org.project.memospace.application.service.command.card;

import org.project.memospace.application.service.Command;

import java.util.List;

public record CreateCardCommand(Long deckId, String front, String back, List<String> tags) implements Command {
}