package com.example.cardservice.application.handler.card;

import lombok.RequiredArgsConstructor;
import org.project.memospace.application.cqrs.CommandHandler;
import org.project.memospace.application.cqrs.command.card.UpdateCardCommand;
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
        Card card = cardRepository.findById(command.getCardId())
                .orElseThrow(() -> new CardNotFoundException(command.getCardId()));

        Card updatedCard = card.updateContent(command.getFront(), command.getBack(), command.getTags());
        return cardRepository.save(updatedCard);
    }
}