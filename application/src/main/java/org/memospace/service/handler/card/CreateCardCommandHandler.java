package org.memospace.service.handler.card;

import lombok.RequiredArgsConstructor;
import org.memospace.service.CommandHandler;
import org.memospace.service.command.card.CreateCardCommand;
import org.memospace.exception.DeckNotFoundException;
import org.memospace.model.Card;
import org.memospace.port.CardRepositoryPort;
import org.memospace.port.DeckRepositoryPort;
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