package com.example.cardservice.application.command;

import com.example.cardservice.application.Command;

import java.util.List;

public record UpdateCardCommand(Long cardId, String front, String back, List<String> tags) implements Command {
}
