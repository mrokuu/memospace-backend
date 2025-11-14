package org.memospace.service.handler.deck;

import lombok.RequiredArgsConstructor;
import org.memospace.service.CommandHandler;
import org.memospace.service.command.deck.UpdateDeckCommand;
import org.memospace.exception.DeckNotFoundException;
import org.memospace.model.Deck;
import org.memospace.port.DeckRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class UpdateDeckCommandHandler implements CommandHandler<UpdateDeckCommand, Deck> {

    private final DeckRepositoryPort deckRepository;

    @Override
    public Deck handle(UpdateDeckCommand command) {
        Deck deck = deckRepository.findById(command.deckId())
                .orElseThrow(() -> new DeckNotFoundException(command.deckId()));

        Deck updatedDeck = deck.updateDetails(command.name(), command.description());
        return deckRepository.save(updatedDeck);
    }
}