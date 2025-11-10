package org.project.memospace.adapter.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update a note")
public class UpdateNoteRequest {

    @Schema(description = "Deck ID (optional)", example = "1")
    private Long deckId;

    @Schema(description = "Field values mapped by field name", example = "{\"Front\": \"Updated Hello\", \"Back\": \"Updated Cześć\"}")
    private Map<@Size(max = 64) String, @Size(max = 16000) String> fieldValues;

    @Schema(description = "Set of tags", example = "[\"lang::en\", \"vocab\", \"updated\"]")
    private Set<@Size(max = 64) String> tags;
}