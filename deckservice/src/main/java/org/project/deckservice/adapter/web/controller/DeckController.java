package org.project.deckservice.adapter.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.deckservice.adapter.web.dto.CreateDeckRequest;
import org.project.deckservice.adapter.web.dto.UpdateDeckRequest;
import org.project.deckservice.application.CommandBus;
import org.project.deckservice.application.QueryBus;
import org.project.deckservice.application.command.CreateDeckCommand;
import org.project.deckservice.application.command.DeleteDeckCommand;
import org.project.deckservice.application.command.UpdateDeckCommand;
import org.project.deckservice.application.query.GetDeckQuery;
import org.project.deckservice.application.query.ListDecksQuery;
import org.project.deckservice.domain.model.Deck;
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

    @GetMapping
    @Operation(summary = "List all decks", description = "Retrieve all available decks")
    @ApiResponse(responseCode = "200", description = "Decks retrieved successfully")
    public ResponseEntity<List<Deck>> listDecks() {
        List<Deck> decks = queryBus.send(new ListDecksQuery());
        return ResponseEntity.ok(decks);
    }

    @PostMapping
    @Operation(summary = "Create a new deck", description = "Create a new flashcard deck")
    @ApiResponse(responseCode = "201", description = "Deck created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    public ResponseEntity<Deck> createDeck(@Valid @RequestBody CreateDeckRequest request) {
        CreateDeckCommand command = new CreateDeckCommand(request.name(), request.description());
        Deck deck = commandBus.send(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(deck);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get deck by ID", description = "Retrieve a specific deck by its ID")
    @ApiResponse(responseCode = "200", description = "Deck retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Deck not found")
    public ResponseEntity<Deck> getDeck(@Parameter(description = "Deck ID") @PathVariable Long id) {
        Deck deck = queryBus.send(new GetDeckQuery(id));
        return ResponseEntity.ok(deck);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update deck", description = "Update an existing deck")
    @ApiResponse(responseCode = "200", description = "Deck updated successfully")
    @ApiResponse(responseCode = "404", description = "Deck not found")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    public ResponseEntity<Deck> updateDeck(@Parameter(description = "Deck ID") @PathVariable Long id,
                                              @Valid @RequestBody UpdateDeckRequest request) {
        UpdateDeckCommand command = new UpdateDeckCommand(id, request.name(), request.description());
        Deck deck = commandBus.send(command);
        return ResponseEntity.ok(deck);
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
