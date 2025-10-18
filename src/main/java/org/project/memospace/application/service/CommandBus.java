package org.project.memospace.application.service;

/**
 * Command bus interface for the CQRS pattern.
 * Routes commands to their appropriate handlers.
 */
public interface CommandBus {

    /**
     * Send a command to its handler.
     *
     * @param command The command to send
     * @param <R> The result type
     * @return The result of the command execution
     */
    <R> R send(Command command);
}