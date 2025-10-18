package org.project.memospace.application.service.command.card;

import org.project.memospace.application.service.Command;

public record DeleteCardCommand(Long cardId) implements Command {
}