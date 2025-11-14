package org.memospace.service.handler.card;

import lombok.RequiredArgsConstructor;
import org.memospace.service.CommandHandler;
import org.memospace.service.command.card.ReviewCardCommand;
import org.memospace.exception.CardNotFoundException;
import org.memospace.model.Card;
import org.memospace.model.ReviewLog;
import org.memospace.port.CardRepositoryPort;
import org.memospace.port.ReviewLogRepositoryPort;
import org.memospace.service.SchedulerService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class ReviewCardCommandHandler implements CommandHandler<ReviewCardCommand, Card> {

    private final CardRepositoryPort cardRepository;
    private final ReviewLogRepositoryPort reviewLogRepository;
    private final SchedulerService schedulerService;

    @Override
    public Card handle(ReviewCardCommand command) {
        Card card = cardRepository.findById(command.cardId())
                .orElseThrow(() -> new CardNotFoundException(command.cardId()));

        // Update card scheduling
        Card updatedCard = schedulerService.updateCardScheduling(card, command.reviewResult());
        Card savedCard = cardRepository.save(updatedCard);

        // Save review log with card state after review
        ReviewLog reviewLog = ReviewLog.create(
                command.cardId(),
                savedCard.deckId(),
                command.reviewResult(),
                savedCard.easeFactor(),
                savedCard.intervalDays()
        );
        reviewLogRepository.save(reviewLog);

        return savedCard;
    }
}