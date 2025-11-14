package org.memospace.service.command.card;

import org.memospace.service.Command;

import java.util.List;

public record UpdateCardCommand(Long cardId, String front, String back, List<String> tags) implements Command {
}