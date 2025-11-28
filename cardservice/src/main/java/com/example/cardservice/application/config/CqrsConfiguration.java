package com.example.cardservice.application.config;

import com.example.cardservice.application.SimpleCommandBus;
import com.example.cardservice.application.SimpleQueryBus;
import com.example.cardservice.application.command.CreateCardCommand;
import com.example.cardservice.application.command.DeleteCardCommand;
import com.example.cardservice.application.command.UpdateCardCommand;
import com.example.cardservice.application.handler.card.CreateCardCommandHandler;
import com.example.cardservice.application.handler.card.DeleteCardCommandHandler;
import com.example.cardservice.application.handler.card.GetCardQueryHandler;
import com.example.cardservice.application.handler.card.UpdateCardCommandHandler;
import com.example.cardservice.application.query.GetCardQuery;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CqrsConfiguration {

    private final SimpleCommandBus commandBus;
    private final SimpleQueryBus queryBus;

    // Command handlers
    private final CreateCardCommandHandler createCardCommandHandler;
    private final UpdateCardCommandHandler updateCardCommandHandler;
    private final DeleteCardCommandHandler deleteCardCommandHandler;
    // Query handlers
    private final GetCardQueryHandler getCardQueryHandler;

    @PostConstruct
    public void registerHandlers() {
        // Register command handlers
        commandBus.registerHandler(CreateCardCommand.class, createCardCommandHandler);
        commandBus.registerHandler(UpdateCardCommand.class, updateCardCommandHandler);
        commandBus.registerHandler(DeleteCardCommand.class, deleteCardCommandHandler);
        // Register query handlers
        queryBus.registerHandler(GetCardQuery.class, getCardQueryHandler);

    }
}
