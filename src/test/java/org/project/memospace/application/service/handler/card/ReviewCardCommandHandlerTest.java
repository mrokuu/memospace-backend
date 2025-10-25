package org.project.memospace.application.service.handler.card;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.memospace.application.service.command.card.ReviewCardCommand;
import org.project.memospace.domain.exception.CardNotFoundException;
import org.project.memospace.domain.model.Card;
import org.project.memospace.domain.model.ReviewLog;
import org.project.memospace.domain.model.ReviewResult;
import org.project.memospace.domain.port.CardRepositoryPort;
import org.project.memospace.domain.port.ReviewLogRepositoryPort;
import org.project.memospace.domain.service.SchedulerService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.project.memospace.util.TestFixtures.*;

@ExtendWith(MockitoExtension.class)
class ReviewCardCommandHandlerTest {

    @Mock
    private CardRepositoryPort cardRepository;

    @Mock
    private ReviewLogRepositoryPort reviewLogRepository;

    @Mock
    private SchedulerService schedulerService;

    @InjectMocks
    private ReviewCardCommandHandler handler;

    @Test
    void shouldReviewCardSuccessfully() {
        // Given
        Card existingCard = newCardWithId(CARD_ID);
        Card scheduledCard = cardBuilder().id(CARD_ID).intervalDays(6).repetitions(1).build();

        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.of(existingCard));
        when(schedulerService.updateCardScheduling(any(Card.class), any(ReviewResult.class))).thenReturn(scheduledCard);
        when(cardRepository.save(scheduledCard)).thenReturn(scheduledCard);
        when(reviewLogRepository.save(any(ReviewLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReviewCardCommand command = new ReviewCardCommand(CARD_ID, goodReview());

        // When
        Card result = handler.handle(command);

        // Then
        assertNotNull(result);
        assertEquals(CARD_ID, result.id());
        verify(cardRepository).findById(CARD_ID);
        verify(schedulerService).updateCardScheduling(any(Card.class), any(ReviewResult.class));
        verify(cardRepository).save(any(Card.class));
        verify(reviewLogRepository).save(any(ReviewLog.class));
    }

    @Test
    void shouldThrowExceptionWhenCardNotFound() {
        // Given
        when(cardRepository.findById(999L)).thenReturn(Optional.empty());
        ReviewCardCommand command = new ReviewCardCommand(999L, goodReview());

        // When & Then
        assertThrows(CardNotFoundException.class, () -> handler.handle(command));
        verify(cardRepository).findById(999L);
        verifyNoInteractions(schedulerService);
        verify(cardRepository, never()).save(any(Card.class));
        verify(reviewLogRepository, never()).save(any(ReviewLog.class));
    }

    @Test
    void shouldCreateReviewLogAfterSavingCard() {
        // Given
        Card existingCard = newCardWithId(CARD_ID);
        Card scheduledCard = cardBuilder()
                .id(CARD_ID)
                .intervalDays(6)
                .repetitions(1)
                .easeFactor(2.6)
                .build();

        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.of(existingCard));
        when(schedulerService.updateCardScheduling(any(), any())).thenReturn(scheduledCard);
        when(cardRepository.save(any())).thenReturn(scheduledCard);
        when(reviewLogRepository.save(any(ReviewLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReviewCardCommand command = new ReviewCardCommand(CARD_ID, goodReview());

        // When
        handler.handle(command);

        // Then
        var inOrder = inOrder(cardRepository, reviewLogRepository);
        inOrder.verify(cardRepository).save(any(Card.class));
        inOrder.verify(reviewLogRepository).save(any(ReviewLog.class));
    }

    @Test
    void shouldSaveReviewLogWithUpdatedCardState() {
        // Given
        Card existingCard = newCardWithId(CARD_ID);
        Card scheduledCard = cardBuilder()
                .id(CARD_ID)
                .deckId(DECK_ID)
                .intervalDays(15)
                .easeFactor(2.8)
                .build();

        ReviewResult review = ReviewResult.create(4);

        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.of(existingCard));
        when(schedulerService.updateCardScheduling(any(), any())).thenReturn(scheduledCard);
        when(cardRepository.save(any())).thenReturn(scheduledCard);
        when(reviewLogRepository.save(any(ReviewLog.class))).thenAnswer(invocation -> {
            ReviewLog log = invocation.getArgument(0);
            assertEquals(CARD_ID, log.cardId());
            assertEquals(DECK_ID, log.deckId());
            assertEquals(4, log.quality());
            assertEquals(2.8, log.easeFactorAfter());
            assertEquals(15, log.intervalAfter());
            return log;
        });

        ReviewCardCommand command = new ReviewCardCommand(CARD_ID, review);

        // When
        handler.handle(command);

        // Then
        verify(reviewLogRepository).save(any(ReviewLog.class));
    }

    @Test
    void shouldHandleFailedReview() {
        // Given
        Card existingCard = experiencedCard();
        Card resetCard = cardBuilder()
                .id(CARD_ID)
                .repetitions(0)
                .intervalDays(1)
                .easeFactor(2.5)
                .build();

        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.of(existingCard));
        when(schedulerService.updateCardScheduling(any(Card.class), any(ReviewResult.class))).thenReturn(resetCard);
        when(cardRepository.save(resetCard)).thenReturn(resetCard);
        when(reviewLogRepository.save(any(ReviewLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReviewCardCommand command = new ReviewCardCommand(CARD_ID, failedReview());

        // When
        Card result = handler.handle(command);

        // Then
        assertEquals(0, result.repetitions());
        assertEquals(1, result.intervalDays());
        verify(reviewLogRepository).save(any(ReviewLog.class));
    }

    @Test
    void shouldHandlePerfectReview() {
        // Given
        Card existingCard = newCardWithId(CARD_ID);
        Card scheduledCard = cardBuilder()
                .id(CARD_ID)
                .intervalDays(6)
                .repetitions(1)
                .easeFactor(2.6)
                .build();

        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.of(existingCard));
        when(schedulerService.updateCardScheduling(any(Card.class), any(ReviewResult.class))).thenReturn(scheduledCard);
        when(cardRepository.save(scheduledCard)).thenReturn(scheduledCard);
        when(reviewLogRepository.save(any(ReviewLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReviewCardCommand command = new ReviewCardCommand(CARD_ID, perfectReview());

        // When
        Card result = handler.handle(command);

        // Then
        assertNotNull(result);
        verify(schedulerService).updateCardScheduling(any(Card.class), any(ReviewResult.class));
    }
}
