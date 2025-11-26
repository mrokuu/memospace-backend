package com.example.cardservice.application.handler.card;

import com.example.cardservice.adapter.persistance.repository.CardRepositoryAdapter;
import com.example.cardservice.application.CommandHandler;
import com.example.cardservice.application.command.CreateCardCommand;
import com.example.cardservice.domain.model.Card;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class CreateCardCommandHandler implements CommandHandler<CreateCardCommand, Card> {

    private final CardRepositoryAdapter cardRepositoryAdapter;

    @Override
    public Card handle(CreateCardCommand command) {
//        if (!deckRepository.existsById(command.getDeckId())) {
//            throw new CardNotFoundException(command.getDeckId());
//        }

        Card card = Card.create(command.getDeckId(), command.getFront(), command.getBack(), command.getTags());
        return cardRepositoryAdapter.save(card);
    }
}