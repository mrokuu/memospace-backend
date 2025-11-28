package com.example.cardservice.application.handler.card;

import com.example.cardservice.adapter.client.deck.DeckClient;
import com.example.cardservice.adapter.persistance.repository.CardRepositoryAdapter;
import com.example.cardservice.application.CommandHandler;
import com.example.cardservice.application.command.CreateCardCommand;
import com.example.cardservice.domain.exception.CardNotFoundException;
import com.example.cardservice.domain.model.Card;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class CreateCardCommandHandler implements CommandHandler<CreateCardCommand, Card> {

    private final CardRepositoryAdapter cardRepositoryAdapter;
    private final DeckClient deckClient;

    @Override
    public Card handle(CreateCardCommand command) {
        if (isABoolean(command)) {
            throw new CardNotFoundException(command.deckId());
        }

        Card card = Card.create(command.deckId(), command.front(), command.back(), command.tags());
        return cardRepositoryAdapter.save(card);
    }

    private boolean isABoolean(CreateCardCommand command) {
        var result =  deckClient.findByDeckId(String.valueOf(command.deckId()));
        return result.isEmpty();
    }
}