package org.project.memospace.application.service.command.card;

import org.project.memospace.application.service.Command;
import org.project.memospace.domain.model.ReviewResult;

public record ReviewCardCommand(Long cardId, ReviewResult reviewResult) implements Command {
}