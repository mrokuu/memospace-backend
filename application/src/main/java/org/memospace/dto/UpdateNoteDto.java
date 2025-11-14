package org.memospace.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.memospace.model.Note;

@Schema(description = "Result of updating a note")
public record UpdateNoteDto(
        @Schema(description = "The updated note")
        Note note,

        @Schema(description = "Number of cards generated from the note", example = "2")
        int cardCount) { }
