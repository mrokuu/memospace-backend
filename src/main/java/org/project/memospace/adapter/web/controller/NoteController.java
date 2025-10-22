package org.project.memospace.adapter.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.memospace.adapter.web.dto.*;
import org.project.memospace.adapter.web.mapper.CardWebMapper;
import org.project.memospace.adapter.web.mapper.NoteWebMapper;
import org.project.memospace.application.service.CommandBus;
import org.project.memospace.application.service.QueryBus;
import org.project.memospace.application.service.command.note.CreateNoteCommand;
import org.project.memospace.application.service.command.note.DeleteNoteCommand;
import org.project.memospace.application.service.command.note.RegenerateCardsCommand;
import org.project.memospace.application.service.command.note.UpdateNoteCommand;
import org.project.memospace.application.service.query.note.GetNoteCardsQuery;
import org.project.memospace.application.service.query.note.GetNoteQuery;
import org.project.memospace.application.service.query.note.SearchNotesQuery;
import org.project.memospace.domain.model.Card;
import org.project.memospace.domain.model.Note;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/notes")
@Tag(name = "Notes", description = "Operations related to notes")
@RequiredArgsConstructor
public class NoteController {

    private final CommandBus commandBus;
    private final QueryBus queryBus;
    private final NoteWebMapper noteWebMapper;
    private final CardWebMapper cardWebMapper;

    @PostMapping
    @Operation(summary = "Create a new note", description = "Creates a new note and generates cards from templates")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Note created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Deck or note type not found")
    })
    public ResponseEntity<CreateNoteResponse> createNote(
            @Valid @RequestBody CreateNoteRequest request) {
        Note note = noteWebMapper.toDomain(request);
        CreateNoteCommand command = new CreateNoteCommand(note);
        CreateNoteDto result = commandBus.send(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(noteWebMapper.toCreateResponse(result));
    }

    @GetMapping
    @Operation(summary = "Search notes", description = "Search notes with optional filters")
    @ApiResponse(responseCode = "200", description = "Notes retrieved successfully")
    public ResponseEntity<List<NoteDto>> searchNotes(
            @Parameter(description = "Filter by deck ID") @RequestParam(required = false) Long deckId,
            @Parameter(description = "Filter by tag") @RequestParam(required = false) String tag,
            @Parameter(description = "Search query") @RequestParam(required = false) String q,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        SearchNotesQuery query = new SearchNotesQuery(deckId, tag, q, page, size);
        var notes = queryBus.send(query)
                .stream()
                .map(noteWebMapper::toDto)
                .toList();

        return ResponseEntity.ok(notes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get note by ID", description = "Retrieves a specific note by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Note found"),
            @ApiResponse(responseCode = "404", description = "Note not found")
    })
    public ResponseEntity<NoteDto> getNote(
            @Parameter(description = "Note ID") @PathVariable UUID id) {
        Note note = queryBus.send(new GetNoteQuery(id));
        return ResponseEntity.ok(noteWebMapper.toDto(note));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update note", description = "Updates an existing note and regenerates cards")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Note updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Note or deck not found")
    })
    public ResponseEntity<CreateNoteResponse> updateNote(
            @Parameter(description = "Note ID") @PathVariable UUID id,
            @Valid @RequestBody UpdateNoteRequest request) {
        UpdateNoteCommand command = new UpdateNoteCommand(id, request.getDeckId(), request.getFieldValues(), request.getTags());
        UpdateNoteDto result = commandBus.send(command);
        return ResponseEntity.ok(new CreateNoteResponse(
                noteWebMapper.toDto(result.note()), result.cardCount()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete note", description = "Deletes a note and all its generated cards")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Note deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Note not found")
    })
    public ResponseEntity<Void> deleteNote(
            @Parameter(description = "Note ID") @PathVariable UUID id) {
        commandBus.send(new DeleteNoteCommand(id));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/cards")
    @Operation(summary = "Get cards for note", description = "Retrieves all cards generated from a note")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cards retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Note not found")
    })
    public ResponseEntity<List<CardDto>> getNoteCards(
            @Parameter(description = "Note ID") @PathVariable UUID id) {
        List<Card> cards = queryBus.send(new GetNoteCardsQuery(id));
        List<CardDto> dtos = cards.stream()
                .map(cardWebMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{id}/regenerate-cards")
    @Operation(summary = "Regenerate cards for note", description = "Regenerates cards for a note")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cards regenerated successfully"),
            @ApiResponse(responseCode = "404", description = "Note not found")
    })
    public ResponseEntity<Map<String, Object>> regenerateCards(
            @Parameter(description = "Note ID") @PathVariable UUID id,
            @Parameter(description = "Regeneration mode") @RequestParam(defaultValue = "REPLACE") RegenerateMode mode) {
        RegenerateCardsCommand command = new RegenerateCardsCommand(id, mode);
        RegenerateCardsDto result = commandBus.send(command);
        return ResponseEntity.ok(Map.of(
                "cardCount", result.cardCount(),
                "mode", result.mode()
        ));
    }
}