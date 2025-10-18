package org.project.memospace.application.service.handler.deck;

import lombok.RequiredArgsConstructor;
import org.project.memospace.application.service.CommandHandler;
import org.project.memospace.application.service.command.deck.UpdateDeckCommand;
import org.project.memospace.domain.exception.DeckNotFoundException;
import org.project.memospace.domain.model.Deck;
import org.project.memospace.domain.port.DeckRepositoryPort;
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