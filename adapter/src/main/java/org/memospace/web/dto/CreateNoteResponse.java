package org.memospace.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response after creating a note")
public class CreateNoteResponse {

    @Schema(description = "Created note")
    private NoteDto note;

    @Schema(description = "Number of cards generated", example = "2")
    private int cardCount;
}