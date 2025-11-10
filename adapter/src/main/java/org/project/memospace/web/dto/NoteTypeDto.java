package org.project.memospace.adapter.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Note type response")
public class NoteTypeDto {

    @Schema(description = "Note type ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Note type name", example = "Basic")
    private String name;

    @Schema(description = "List of field names", example = "[\"Front\", \"Back\"]")
    private List<String> fields;

    @Schema(description = "List of card templates")
    private List<CardTemplateDto> templates;

    @Schema(description = "CSS styles for cards", example = ".cloze{font-weight:bold;}")
    private String css;

    @Schema(description = "Creation timestamp", example = "2024-01-01T12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2024-01-01T12:00:00")
    private LocalDateTime updatedAt;
}