package org.project.memospace.adapter.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Note response")
public class NoteDto {

    @Schema(description = "Note ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Deck ID", example = "1")
    private Long deckId;

    @Schema(description = "Note type ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID noteTypeId;

    @Schema(description = "Field values mapped by field name", example = "{\"Front\": \"Hello\", \"Back\": \"Cześć\"}")
    private Map<String, String> fieldValues;

    @Schema(description = "Set of tags", example = "[\"lang::en\", \"vocab\"]")
    private Set<String> tags;

    @Schema(description = "Creation timestamp", example = "2024-01-01T12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2024-01-01T12:00:00")
    private LocalDateTime updatedAt;
}