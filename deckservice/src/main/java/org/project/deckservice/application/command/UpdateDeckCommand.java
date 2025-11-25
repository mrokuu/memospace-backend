package org.project.deckservice.application.command;

import lombok.Value;
import org.project.deckservice.application.Command;

@Value
public class UpdateDeckCommand implements Command {
    Long deckId;
    String name;
    String description;
}
