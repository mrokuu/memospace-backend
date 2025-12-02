package com.example.cardservice.application.handler.card;

import com.example.cardservice.adapter.persistance.repository.CardRepositoryAdapter;
import com.example.cardservice.application.CommandHandler;
import com.example.cardservice.application.command.DeleteCardCommand;
import com.example.cardservice.domain.exception.CardNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
@Transactional
public class DeleteCardCommandHandler implements CommandHandler<DeleteCardCommand, Void> {

    private final CardRepositoryAdapter cardRepositoryAdapter;

    @Override
    public Void handle(DeleteCardCommand command) {
        if (!cardRepositoryAdapter.existsById(command.cardId())) {
            throw new CardNotFoundException(command.cardId());
        }

        cardRepositoryAdapter.deleteById(command.cardId());
        return null;
    }
}