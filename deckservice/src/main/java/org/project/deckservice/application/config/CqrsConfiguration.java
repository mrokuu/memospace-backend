package org.project.deckservice.application.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.project.deckservice.application.SimpleCommandBus;
import org.project.deckservice.application.SimpleQueryBus;
import org.project.deckservice.application.command.CreateDeckCommand;
import org.project.deckservice.application.command.DeleteDeckCommand;
import org.project.deckservice.application.command.UpdateDeckCommand;
import org.project.deckservice.application.handler.deck.*;
import org.project.deckservice.application.query.GetDeckQuery;
import org.project.deckservice.application.query.ListDecksQuery;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CqrsConfiguration {

    private final SimpleCommandBus commandBus;
    private final SimpleQueryBus queryBus;

    // Command handlers
    private final CreateDeckCommandHandler createDeckCommandHandler;
    private final UpdateDeckCommandHandler updateDeckCommandHandler;
    private final DeleteDeckCommandHandler deleteDeckCommandHandler;

    // Query handlers
    private final GetDeckQueryHandler getDeckQueryHandler;
    private final ListDecksQueryHandler listDecksQueryHandler;

    @PostConstruct
    public void registerHandlers() {
        // Register command handlers
        commandBus.registerHandler(CreateDeckCommand.class, createDeckCommandHandler);
        commandBus.registerHandler(UpdateDeckCommand.class, updateDeckCommandHandler);
        commandBus.registerHandler(DeleteDeckCommand.class, deleteDeckCommandHandler);

        // Register query handlers
        queryBus.registerHandler(GetDeckQuery.class, getDeckQueryHandler);
        queryBus.registerHandler(ListDecksQuery.class, listDecksQueryHandler);
    }
}
