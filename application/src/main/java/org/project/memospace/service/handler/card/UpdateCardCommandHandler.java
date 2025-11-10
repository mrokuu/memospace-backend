package org.project.memospace.application.service.handler.card;

import lombok.RequiredArgsConstructor;
import org.project.memospace.application.service.CommandHandler;
import org.project.memospace.application.service.command.card.UpdateCardCommand;
import org.project.memospace.domain.exception.CardNotFoundException;
import org.project.memospace.domain.model.Card;
import org.project.memospace.domain.port.CardRepositoryPort;
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