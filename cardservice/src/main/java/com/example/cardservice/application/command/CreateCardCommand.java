package com.example.cardservice.application.command;

import com.example.cardservice.application.Command;
import lombok.Value;

import java.util.List;

@Value
public class CreateCardCommand implements Command {
    Long deckId;
    String front;
    String back;
    List<String> tags;
}
