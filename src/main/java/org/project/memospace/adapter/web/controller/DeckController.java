package org.project.memospace.adapter.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.memospace.adapter.web.dto.CreateDeckRequest;
import org.project.memospace.adapter.web.dto.DeckDto;
import org.project.memospace.adapter.web.dto.UpdateDeckRequest;
import org.project.memospace.adapter.web.mapper.DeckWebMapper;
import org.project.memospace.application.service.CommandBus;
import org.project.memospace.application.service.QueryBus;
import org.project.memospace.application.service.command.deck.CreateDeckCommand;
import org.project.memospace.application.service.command.deck.DeleteDeckCommand;
import org.project.memospace.application.service.command.deck.UpdateDeckCommand;
import org.project.memospace.application.service.query.deck.GetDeckQuery;
import org.project.memospace.application.service.query.deck.ListDecksQuery;
import org.project.memospace.domain.model.Deck;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/decks")
@Tag(name = "Decks", description = "Deck management operations")
@RequiredArgsConstructor
public class DeckController {

    private final CommandBus commandBus;
    private final QueryBus queryBus;
    private final DeckWebMapper mapper;

    @GetMapping
    @Operation(summary = "List all decks", description = "Retrieve all available decks")
    @ApiResponse(responseCode = "200", description = "Decks retrieved successfully")
    public ResponseEntity<List<DeckDto>> listDecks() {
        List<Deck> decks = queryBus.send(new ListDecksQuery());
        List<DeckDto> deckDtos = decks.stream()
                .map(mapper::toDto)
                .toList();
        return ResponseEntity.ok(deckDtos);
    }

    @PostMapping
    @Operation(summary = "Create a new deck", description = "Create a new flashcard deck")
    @ApiResponse(responseCode = "201", description = "Deck created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    public ResponseEntity<DeckDto> createDeck(@Valid @RequestBody CreateDeckRequest request) {
        CreateDeckCommand command = new CreateDeckCommand(request.name(), request.description());
        Deck deck = commandBus.send(command);
        DeckDto deckDto = mapper.toDto(deck);
        return ResponseEntity.status(HttpStatus.CREATED).body(deckDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get deck by ID", description = "Retrieve a specific deck by its ID")
    @ApiResponse(responseCode = "200", description = "Deck retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Deck not found")
    public ResponseEntity<DeckDto> getDeck(@Parameter(description = "Deck ID") @PathVariable Long id) {
        Deck deck = queryBus.send(new GetDeckQuery(id));
        DeckDto deckDto = mapper.toDto(deck);
        return ResponseEntity.ok(deckDto);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update deck", description = "Update an existing deck")
    @ApiResponse(responseCode = "200", description = "Deck updated successfully")
    @ApiResponse(responseCode = "404", description = "Deck not found")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    public ResponseEntity<DeckDto> updateDeck(@Parameter(description = "Deck ID") @PathVariable Long id,
                                            @Valid @RequestBody UpdateDeckRequest request) {
        UpdateDeckCommand command = new UpdateDeckCommand(id, request.name(), request.description());
        Deck deck = commandBus.send(command);
        DeckDto deckDto = mapper.toDto(deck);
        return ResponseEntity.ok(deckDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete deck", description = "Delete a deck and all its cards")
    @ApiResponse(responseCode = "204", description = "Deck deleted successfully")
    @ApiResponse(responseCode = "404", description = "Deck not found")
    public ResponseEntity<Void> deleteDeck(@Parameter(description = "Deck ID") @PathVariable Long id) {
        commandBus.send(new DeleteDeckCommand(id));
        return ResponseEntity.noContent().build();
    }
}