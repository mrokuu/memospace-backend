package org.project.memospace.application.service.command.card;

import org.project.memospace.application.service.Command;

import java.util.List;

public record UpdateCardCommand(Long cardId, String front, String back, List<String> tags) implements Command {
}