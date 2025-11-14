package org.memospace.service.handler.deck;

import lombok.RequiredArgsConstructor;
import org.memospace.service.CommandHandler;
import org.memospace.service.command.deck.DeleteDeckCommand;
import org.memospace.exception.DeckNotFoundException;
import org.memospace.port.DeckRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class DeleteDeckCommandHandler implements CommandHandler<DeleteDeckCommand, Void> {

    private final DeckRepositoryPort deckRepository;

    @Override
    public Void handle(DeleteDeckCommand command) {
        if (!deckRepository.existsById(command.deckId())) {
            throw new DeckNotFoundException(command.deckId());
        }

        deckRepository.deleteById(command.deckId());
        return null;
    }
}