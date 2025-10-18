package org.project.memospace.adapter.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.project.memospace.domain.model.Note;

@Schema(description = "Result of creating a new note")
public record CreateNoteDto(
        @Schema(description = "The created note")
        Note note,

        @Schema(description = "Number of cards generated from the note", example = "2")
        int cardCount) {
}
