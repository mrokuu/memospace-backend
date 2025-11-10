package org.project.memospace.adapter.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.memospace.adapter.web.dto.CardDto;
import org.project.memospace.adapter.web.dto.CreateCardRequest;
import org.project.memospace.adapter.web.dto.UpdateCardRequest;
import org.project.memospace.adapter.web.mapper.CardWebMapper;
import org.project.memospace.application.service.CommandBus;
import org.project.memospace.application.service.QueryBus;
import org.project.memospace.application.service.command.card.CreateCardCommand;
import org.project.memospace.application.service.command.card.DeleteCardCommand;
import org.project.memospace.application.service.command.card.UpdateCardCommand;
import org.project.memospace.application.service.query.card.GetCardQuery;
import org.project.memospace.application.service.query.card.SearchCardsQuery;
import org.project.memospace.domain.model.Card;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cards")
@Tag(name = "Cards", description = "Card management operations")
@RequiredArgsConstructor
public class CardController {

    private final CommandBus commandBus;
    private final QueryBus queryBus;
    private final CardWebMapper mapper;

    @GetMapping
    @Operation(summary = "Search cards", description = "Search and filter cards with pagination")
    @ApiResponse(responseCode = "200", description = "Cards retrieved successfully")
    public ResponseEntity<List<CardDto>> searchCards(
            @Parameter(description = "Deck ID to filter by") @RequestParam(required = false) Long deckId,
            @Parameter(description = "Search term to match against front, back, or tags") @RequestParam(required = false) String search,
            @Parameter(description = "Filter for due cards only") @RequestParam(required = false, defaultValue = "false") String due,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {

        Boolean onlyDue = "overdue".equals(due) || "true".equals(due) ? true : null;
        SearchCardsQuery query = new SearchCardsQuery(deckId, search, onlyDue, page, size);
        var cards = queryBus.send(query)
                .stream()
                .map(mapper::toDto)
                .toList();

        return ResponseEntity.ok(cards);
    }

    @PostMapping
    @Operation(summary = "Create a new card", description = "Create a new flashcard in a deck")
    @ApiResponse(responseCode = "201", description = "Card created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "404", description = "Deck not found")
    public ResponseEntity<CardDto> createCard(@Valid @RequestBody CreateCardRequest request) {
        CreateCardCommand command = new CreateCardCommand(request.deckId(), request.front(), request.back(), request.tags() != null ? List.copyOf(request.tags()) : List.of());
        Card card = commandBus.send(command);
        CardDto cardDto = mapper.toDto(card);
        return ResponseEntity.status(HttpStatus.CREATED).body(cardDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get card by ID", description = "Retrieve a specific card by its ID")
    @ApiResponse(responseCode = "200", description = "Card retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Card not found")
    public ResponseEntity<CardDto> getCard(@Parameter(description = "Card ID") @PathVariable Long id) {
        Card card = queryBus.send(new GetCardQuery(id));
        CardDto cardDto = mapper.toDto(card);
        return ResponseEntity.ok(cardDto);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update card", description = "Update an existing card")
    @ApiResponse(responseCode = "200", description = "Card updated successfully")
    @ApiResponse(responseCode = "404", description = "Card not found")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    public ResponseEntity<CardDto> updateCard(@Parameter(description = "Card ID") @PathVariable Long id,
                                            @Valid @RequestBody UpdateCardRequest request) {
        UpdateCardCommand command = new UpdateCardCommand(id, request.front(), request.back(), request.tags() != null ? List.copyOf(request.tags()) : List.of());
        Card card = commandBus.send(command);
        CardDto cardDto = mapper.toDto(card);
        return ResponseEntity.ok(cardDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete card", description = "Delete a card")
    @ApiResponse(responseCode = "204", description = "Card deleted successfully")
    @ApiResponse(responseCode = "404", description = "Card not found")
    public ResponseEntity<Void> deleteCard(@Parameter(description = "Card ID") @PathVariable Long id) {
        commandBus.send(new DeleteCardCommand(id));
        return ResponseEntity.noContent().build();
    }
}