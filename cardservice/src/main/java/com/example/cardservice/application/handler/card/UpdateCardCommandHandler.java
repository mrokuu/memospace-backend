package com.example.cardservice.application.handler.card;

import com.example.cardservice.adapter.persistance.repository.CardRepositoryAdapter;
import com.example.cardservice.application.CommandHandler;
import com.example.cardservice.application.command.UpdateCardCommand;
import com.example.cardservice.domain.exception.CardNotFoundException;
import com.example.cardservice.domain.model.Card;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class UpdateCardCommandHandler implements CommandHandler<UpdateCardCommand, Card> {

    private final CardRepositoryAdapter cardRepositoryAdapter;

    @Override
    public Card handle(UpdateCardCommand command) {
        Card card = cardRepositoryAdapter.findById(command.cardId())
                .orElseThrow(() -> new CardNotFoundException(command.cardId()));

        Card updatedCard = card.updateContent(command.front(), command.back(), command.tags());
        return cardRepositoryAdapter.save(updatedCard);
    }
}