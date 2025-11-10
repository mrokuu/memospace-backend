package org.project.memospace.application.service.handler.deck;

import lombok.RequiredArgsConstructor;
import org.project.memospace.application.service.CommandHandler;
import org.project.memospace.application.service.command.deck.DeleteDeckCommand;
import org.project.memospace.domain.exception.DeckNotFoundException;
import org.project.memospace.domain.port.DeckRepositoryPort;
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