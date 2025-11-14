package org.memospace.service;

/**
 * Interface for command handlers in the CQRS pattern.
 * Command handlers process commands and execute write operations.
 *
 * @param <C> The command type
 * @param <R> The result type
 */
public interface CommandHandler<C extends Command, R> {

    /**
     * Handle the command and return the result.
     *
     * @param command The command to handle
     * @return The result of the command execution
     */
    R handle(C command);
}