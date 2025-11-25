package org.project.deckservice.application.handler.deck;

import lombok.RequiredArgsConstructor;
import org.project.deckservice.adapter.persistance.repository.DeckRepositoryAdapter;
import org.project.deckservice.application.CommandHandler;
import org.project.deckservice.application.command.DeleteDeckCommand;
import org.project.deckservice.domain.exception.DeckNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class DeleteDeckCommandHandler implements CommandHandler<DeleteDeckCommand, Void> {

    private final DeckRepositoryAdapter deckRepositoryAdapter;

    @Override
    public Void handle(DeleteDeckCommand command) {
        if (!deckRepositoryAdapter.existsById(command.getDeckId())) {
            throw new DeckNotFoundException(command.getDeckId());
        }

        deckRepositoryAdapter.deleteById(command.getDeckId());
        return null;
    }
}