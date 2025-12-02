package com.example.cardservice.application.command;

import com.example.cardservice.application.Command;

public record DeleteCardCommand(Long cardId) implements Command {
}
