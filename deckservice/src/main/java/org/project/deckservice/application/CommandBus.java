package org.project.deckservice.application;

public interface CommandBus {

    <R> R send(Command command);
}
