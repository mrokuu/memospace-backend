package org.project.memospace.application.service.handler.card;

import lombok.RequiredArgsConstructor;
import org.project.memospace.application.service.CommandHandler;
import org.project.memospace.application.service.command.card.CreateCardCommand;
import org.project.memospace.domain.exception.DeckNotFoundException;
import org.project.memospace.domain.model.Card;
import org.project.memospace.domain.port.CardRepositoryPort;
import org.project.memospace.domain.port.DeckRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class CreateCardCommandHandler implements CommandHandler<CreateCardCommand, Card> {

    private final CardRepositoryPort cardRepository;
    private final DeckRepositoryPort deckRepository;

    @Override
    public Card handle(CreateCardCommand command) {
        if (!deckRepository.existsById(command.deckId())) {
            throw new DeckNotFoundException(command.deckId());
        }

        Card card = Card.create(command.deckId(), command.front(), command.back(), command.tags());
        return cardRepository.save(card);
    }
}