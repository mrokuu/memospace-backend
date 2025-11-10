package org.project.memospace.adapter.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.memospace.adapter.web.dto.CardDto;
import org.project.memospace.adapter.web.dto.ReviewLogDto;
import org.project.memospace.adapter.web.dto.ReviewRequest;
import org.project.memospace.adapter.web.mapper.CardWebMapper;
import org.project.memospace.adapter.web.mapper.ReviewLogWebMapper;
import org.project.memospace.application.service.CommandBus;
import org.project.memospace.application.service.QueryBus;
import org.project.memospace.application.service.command.card.ReviewCardCommand;
import org.project.memospace.application.service.query.card.GetNextDueCardsQuery;
import org.project.memospace.application.service.query.review.GetReviewHistoryQuery;
import org.project.memospace.domain.model.Card;
import org.project.memospace.domain.model.ReviewLog;
import org.project.memospace.domain.model.ReviewResult;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@Tag(name = "Reviews", description = "Review and spaced repetition operations")
@RequiredArgsConstructor
public class ReviewController {

    private final CommandBus commandBus;
    private final QueryBus queryBus;
    private final CardWebMapper cardMapper;
    private final ReviewLogWebMapper reviewLogMapper;

    @GetMapping("/next")
    @Operation(summary = "Get next due cards", description = "Retrieve cards that are due for review, ordered by due date")
    @ApiResponse(responseCode = "200", description = "Due cards retrieved successfully")
    public ResponseEntity<List<CardDto>> getNextDueCards(
            @Parameter(description = "Deck ID to filter by") @RequestParam(required = false) Long deckId,
            @Parameter(description = "Maximum number of cards to return") @RequestParam(defaultValue = "20") int limit) {

        GetNextDueCardsQuery query = new GetNextDueCardsQuery(limit, deckId);
        var dueCards = queryBus.send(query)
                .stream()
                .map(cardMapper::toDto)
                .toList();
        return ResponseEntity.ok(dueCards);
    }

    @PostMapping("/{cardId}")
    @Operation(summary = "Review a card", description = "Submit a review for a card and update its scheduling")
    @ApiResponse(responseCode = "200", description = "Review submitted successfully")
    @ApiResponse(responseCode = "404", description = "Card not found")
    @ApiResponse(responseCode = "400", description = "Invalid review data")
    public ResponseEntity<CardDto> reviewCard(
            @Parameter(description = "Card ID to review") @PathVariable Long cardId,
            @Valid @RequestBody ReviewRequest request) {

        ReviewResult reviewResult = ReviewResult.create(request.quality(), request.msSpent());
        ReviewCardCommand command = new ReviewCardCommand(cardId, reviewResult);
        Card updatedCard = commandBus.send(command);
        CardDto cardDto = cardMapper.toDto(updatedCard);
        return ResponseEntity.ok(cardDto);
    }

    @GetMapping("/history")
    @Operation(summary = "Get review history", description = "Retrieve review history with optional filtering")
    @ApiResponse(responseCode = "200", description = "Review history retrieved successfully")
    public ResponseEntity<List<ReviewLogDto>> getReviewHistory(
            @Parameter(description = "Deck ID to filter by") @RequestParam(required = false) Long deckId,
            @Parameter(description = "Start date for filtering")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @Parameter(description = "End date for filtering")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        // Default to last 30 days if no dates specified
        if (from == null) {
            from = LocalDateTime.now().minusDays(30);
        }
        if (to == null) {
            to = LocalDateTime.now();
        }

        GetReviewHistoryQuery query = new GetReviewHistoryQuery(deckId, from, to);
        List<ReviewLog> reviewLogs = queryBus.send(query);
        List<ReviewLogDto> reviewLogDtos = reviewLogs.stream()
                .map(reviewLogMapper::toDto)
                .toList();
        return ResponseEntity.ok(reviewLogDtos);
    }
}