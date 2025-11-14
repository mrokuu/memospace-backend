package org.memospace.service.command.card;

import org.memospace.service.Command;
import org.memospace.model.ReviewResult;

public record ReviewCardCommand(Long cardId, ReviewResult reviewResult) implements Command {
}