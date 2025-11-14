package org.memospace.service.handler.card;

import lombok.RequiredArgsConstructor;
import org.memospace.service.CommandHandler;
import org.memospace.service.command.card.UpdateCardCommand;
import org.memospace.exception.CardNotFoundException;
import org.memospace.model.Card;
import org.memospace.port.CardRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class UpdateCardCommandHandler implements CommandHandler<UpdateCardCommand, Card> {

    private final CardRepositoryPort cardRepository;

    @Override
    public Card handle(UpdateCardCommand command) {
        Card card = cardRepository.findById(command.cardId())
                .orElseThrow(() -> new CardNotFoundException(command.cardId()));

        Card updatedCard = card.updateContent(command.front(), command.back(), command.tags());
        return cardRepository.save(updatedCard);
    }
}