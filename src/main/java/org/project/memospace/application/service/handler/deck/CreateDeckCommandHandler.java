package org.project.memospace.application.service.handler.deck;

import lombok.RequiredArgsConstructor;
import org.project.memospace.application.service.CommandHandler;
import org.project.memospace.application.service.command.deck.CreateDeckCommand;
import org.project.memospace.domain.model.Deck;
import org.project.memospace.domain.port.DeckRepositoryPort;
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