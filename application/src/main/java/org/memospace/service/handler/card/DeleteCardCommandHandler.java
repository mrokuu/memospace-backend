package org.memospace.service.handler.card;

import lombok.RequiredArgsConstructor;
import org.memospace.service.CommandHandler;
import org.memospace.service.command.card.DeleteCardCommand;
import org.memospace.exception.CardNotFoundException;
import org.memospace.port.CardRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class DeleteCardCommandHandler implements CommandHandler<DeleteCardCommand, Void> {

    private final CardRepositoryPort cardRepository;

    @Override
    public Void handle(DeleteCardCommand command) {
        if (!cardRepository.existsById(command.cardId())) {
            throw new CardNotFoundException(command.cardId());
        }

        cardRepository.deleteById(command.cardId());
        return null;
    }
}