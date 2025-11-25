package org.project.deckservice.application.handler.deck;

import lombok.RequiredArgsConstructor;
import org.project.deckservice.adapter.persistance.repository.DeckRepositoryAdapter;
import org.project.deckservice.application.CommandHandler;
import org.project.deckservice.application.command.UpdateDeckCommand;
import org.project.deckservice.domain.exception.DeckNotFoundException;
import org.project.deckservice.domain.model.Deck;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class UpdateDeckCommandHandler implements CommandHandler<UpdateDeckCommand, Deck> {

    private final DeckRepositoryAdapter deckRepositoryAdapter;

    @Override
    public Deck handle(UpdateDeckCommand command) {
        Deck deck = deckRepositoryAdapter.findById(command.getDeckId())
                .orElseThrow(() -> new DeckNotFoundException(command.getDeckId()));

        Deck updatedDeck = deck.updateDetails(command.getName(), command.getDescription());
        return deckRepositoryAdapter.save(updatedDeck);
    }
}