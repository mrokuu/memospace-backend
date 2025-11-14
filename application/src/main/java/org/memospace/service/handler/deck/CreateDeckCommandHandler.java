package org.memospace.service.handler.deck;

import lombok.RequiredArgsConstructor;
import org.memospace.service.CommandHandler;
import org.memospace.service.command.deck.CreateDeckCommand;
import org.memospace.model.Deck;
import org.memospace.port.DeckRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class CreateDeckCommandHandler implements CommandHandler<CreateDeckCommand, Deck> {

    private final DeckRepositoryPort deckRepository;

    @Override
    public Deck handle(CreateDeckCommand command) {
        Deck deck = Deck.create(command.name(), command.description());
        return deckRepository.save(deck);
    }
}