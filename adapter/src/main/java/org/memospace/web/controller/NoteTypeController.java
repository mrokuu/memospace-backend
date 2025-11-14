package org.memospace.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.memospace.web.dto.CreateNoteTypeRequest;
import org.memospace.web.dto.NoteTypeDto;
import org.memospace.web.mapper.NoteTypeWebMapper;
import org.memospace.service.CommandBus;
import org.memospace.service.QueryBus;
import org.memospace.service.command.notetype.CreateNoteTypeCommand;
import org.memospace.service.command.notetype.DeleteNoteTypeCommand;
import org.memospace.service.command.notetype.UpdateNoteTypeCommand;
import org.memospace.service.query.notetype.GetAllNoteTypesQuery;
import org.memospace.service.query.notetype.GetNoteTypeQuery;
import org.memospace.model.NoteType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/note-types")
@Tag(name = "Note Types", description = "Operations related to note types")
@RequiredArgsConstructor
public class NoteTypeController {

    private final CommandBus commandBus;
    private final QueryBus queryBus;
    private final NoteTypeWebMapper webMapper;


    @PostMapping
    @Operation(summary = "Create a new note type", description = "Creates a new note type with fields and templates")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Note type created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Note type with name already exists")
    })
    public ResponseEntity<NoteTypeDto> createNoteType(
            @Valid @RequestBody CreateNoteTypeRequest request) {
        NoteType noteType = webMapper.toDomain(request);
        CreateNoteTypeCommand command = new CreateNoteTypeCommand(noteType);
        NoteType createdNoteType = commandBus.send(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(webMapper.toDto(createdNoteType));
    }

    @GetMapping
    @Operation(summary = "Get all note types", description = "Retrieves all note types")
    @ApiResponse(responseCode = "200", description = "Note types retrieved successfully")
    public ResponseEntity<List<NoteTypeDto>> getAllNoteTypes() {
        var noteTypes = queryBus.send(new GetAllNoteTypesQuery())
                .stream()
                .map(webMapper::toDto)
                .toList();

        return ResponseEntity.ok(noteTypes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get note type by ID", description = "Retrieves a specific note type by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Note type found"),
            @ApiResponse(responseCode = "404", description = "Note type not found")
    })
    public ResponseEntity<NoteTypeDto> getNoteType(
            @Parameter(description = "Note type ID") @PathVariable UUID id) {
        NoteType noteType = queryBus.send(new GetNoteTypeQuery(id));
        return ResponseEntity.ok(webMapper.toDto(noteType));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update note type", description = "Updates an existing note type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Note type updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Note type not found"),
            @ApiResponse(responseCode = "409", description = "Note type with name already exists")
    })
    public ResponseEntity<NoteTypeDto> updateNoteType(
            @Parameter(description = "Note type ID") @PathVariable UUID id,
            @Valid @RequestBody CreateNoteTypeRequest request) {
        NoteType updatedNoteType = webMapper.toDomain(request);
        UpdateNoteTypeCommand command = new UpdateNoteTypeCommand(id, updatedNoteType);
        NoteType result = commandBus.send(command);
        return ResponseEntity.ok(webMapper.toDto(result));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete note type", description = "Deletes a note type if not in use")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Note type deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Note type not found"),
            @ApiResponse(responseCode = "409", description = "Note type is in use and cannot be deleted")
    })
    public ResponseEntity<Void> deleteNoteType(
            @Parameter(description = "Note type ID") @PathVariable UUID id) {
        commandBus.send(new DeleteNoteTypeCommand(id));
        return ResponseEntity.noContent().build();
    }
}