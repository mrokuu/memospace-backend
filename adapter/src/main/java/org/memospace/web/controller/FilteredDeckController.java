package org.memospace.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.memospace.dto.CreateFilteredDeckResult;
import org.memospace.dto.RebuildFilteredDeckResult;
import org.memospace.web.dto.CreateFilteredDeckRequest;
import org.memospace.web.dto.CreateFilteredDeckResponse;
import org.memospace.web.dto.RebuildFilteredDeckResponse;
import org.memospace.web.dto.FilteredDeckDto;
import org.memospace.web.dto.CardDto;
import org.memospace.web.mapper.CardWebMapper;
import org.memospace.web.mapper.FilteredDeckWebMapper;
import org.memospace.service.CommandBus;
import org.memospace.service.QueryBus;
import org.memospace.service.command.filtereddeck.CreateFilteredDeckCommand;
import org.memospace.service.command.filtereddeck.DeleteFilteredDeckCommand;
import org.memospace.service.command.filtereddeck.RebuildFilteredDeckCommand;
import org.memospace.service.query.filtereddeck.GetFilteredDeckQuery;
import org.memospace.service.query.filtereddeck.GetNextForFilteredDeckQuery;
import org.memospace.model.FilteredDeck;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/filtered-decks")
@Tag(name = "Filtered Decks", description = "Filtered deck (custom study) operations")
@RequiredArgsConstructor
public class FilteredDeckController {

    private final CommandBus commandBus;
    private final QueryBus queryBus;
    private final FilteredDeckWebMapper filteredDeckMapper;
    private final CardWebMapper cardMapper;

    @PostMapping
    @Operation(summary = "Create a filtered deck", description = "Create a temporary filtered deck from a query")
    @ApiResponse(responseCode = "201", description = "Filtered deck created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data or query")
    public ResponseEntity<CreateFilteredDeckResponse> createFilteredDeck(@Valid @RequestBody CreateFilteredDeckRequest request) {
        CreateFilteredDeckCommand command = new CreateFilteredDeckCommand(
                request.name(),
                request.ownerDeckId(),
                request.query(),
                request.limit(),
                request.mode(),
                request.returnPolicy(),
                request.expiresAt()
        );

        CreateFilteredDeckResult result = commandBus.send(command);
        CreateFilteredDeckResponse response = filteredDeckMapper.toCreateResponse(
                result.filteredDeck(),
                result.total()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}:rebuild")
    @Operation(summary = "Rebuild a filtered deck", description = "Rebuild the filtered deck membership with the same query")
    @ApiResponse(responseCode = "200", description = "Filtered deck rebuilt successfully")
    @ApiResponse(responseCode = "404", description = "Filtered deck not found")
    public ResponseEntity<RebuildFilteredDeckResponse> rebuildFilteredDeck(
            @Parameter(description = "Filtered deck ID") @PathVariable UUID id) {

        RebuildFilteredDeckCommand command = new RebuildFilteredDeckCommand(id);
        RebuildFilteredDeckResult result = commandBus.send(command);
        RebuildFilteredDeckResponse response = filteredDeckMapper.toRebuildResponse(
                result.filteredDeck(),
                result.total()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a filtered deck", description = "Delete the filtered deck and return all cards")
    @ApiResponse(responseCode = "204", description = "Filtered deck deleted successfully")
    @ApiResponse(responseCode = "404", description = "Filtered deck not found")
    public ResponseEntity<Void> deleteFilteredDeck(
            @Parameter(description = "Filtered deck ID") @PathVariable UUID id) {

        commandBus.send(new DeleteFilteredDeckCommand(id));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get filtered deck", description = "Retrieve filtered deck details by ID")
    @ApiResponse(responseCode = "200", description = "Filtered deck retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Filtered deck not found")
    public ResponseEntity<FilteredDeckDto> getFilteredDeck(
            @Parameter(description = "Filtered deck ID") @PathVariable UUID id) {

        FilteredDeck filteredDeck = queryBus.send(new GetFilteredDeckQuery(id));
        FilteredDeckDto dto = filteredDeckMapper.toDto(filteredDeck);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/next")
    @Operation(summary = "Get next due cards", description = "Get the next due cards for study from this filtered deck")
    @ApiResponse(responseCode = "200", description = "Due cards retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Filtered deck not found")
    public ResponseEntity<List<CardDto>> getNextCards(
            @Parameter(description = "Filtered deck ID") @PathVariable UUID id,
            @Parameter(description = "Number of cards to fetch") @RequestParam(defaultValue = "20") int limit) {

        var cards = queryBus.send(new GetNextForFilteredDeckQuery(id, limit))
                .stream()
                .map(cardMapper::toDto)
                .toList();

        return ResponseEntity.ok(cards);
    }
}
