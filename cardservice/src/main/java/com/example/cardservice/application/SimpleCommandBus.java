package com.example.cardservice.application;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SimpleCommandBus implements CommandBus {

    private final Map<Class<? extends Command>, CommandHandler<? extends Command, ?>> handlers = new HashMap<>();

    public <C extends Command, R> void registerHandler(Class<C> commandClass, CommandHandler<C, R> handler) {
        handlers.put(commandClass, handler);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R send(Command command) {
        CommandHandler<Command, R> handler = (CommandHandler<Command, R>) handlers.get(command.getClass());
        if (handler == null) {
            throw new IllegalArgumentException("No handler registered for command: " + command.getClass().getSimpleName());
        }
        return handler.handle(command);
    }
}
