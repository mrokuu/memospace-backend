package org.project.deckservice.application.command;

import lombok.Value;
import org.project.deckservice.application.Command;

@Value
public class DeleteDeckCommand implements Command {
    Long deckId;
}
