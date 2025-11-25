package org.project.deckservice.application;

public interface CommandHandler<C extends Command, R> {

    R handle(C command);
}
