package org.project.memospace.adapter.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new note")
public class CreateNoteRequest {

    @NotNull(message = "Deck ID is required")
    @Schema(description = "Deck ID", example = "1")
    private Long deckId;

    @NotNull(message = "Note type ID is required")
    @Schema(description = "Note type ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID noteTypeId;

    @Schema(description = "Field values mapped by field name", example = "{\"Front\": \"Hello\", \"Back\": \"Cześć\"}")
    private Map<@Size(max = 64) String, @Size(max = 16000) String> fieldValues;

    @Schema(description = "Set of tags", example = "[\"lang::en\", \"vocab\"]")
    private Set<@Size(max = 64) String> tags;
}