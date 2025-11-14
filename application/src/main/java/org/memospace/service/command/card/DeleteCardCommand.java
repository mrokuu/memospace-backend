package org.memospace.service.command.card;

import org.memospace.service.Command;

public record DeleteCardCommand(Long cardId) implements Command {
}