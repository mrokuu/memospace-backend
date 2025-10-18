package org.project.memospace.application.service.handler.card;

import lombok.RequiredArgsConstructor;
import org.project.memospace.application.service.CommandHandler;
import org.project.memospace.application.service.command.card.DeleteCardCommand;
import org.project.memospace.domain.exception.CardNotFoundException;
import org.project.memospace.domain.port.CardRepositoryPort;
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