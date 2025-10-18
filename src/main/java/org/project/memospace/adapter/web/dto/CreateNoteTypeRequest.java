package org.project.memospace.adapter.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new note type")
public class CreateNoteTypeRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    @Schema(description = "Note type name", example = "Basic")
    private String name;

    @NotEmpty(message = "At least one field is required")
    @Size(max = 20, message = "Cannot exceed 20 fields")
    @Schema(description = "List of field names", example = "[\"Front\", \"Back\"]")
    private List<@NotBlank @Size(max = 64) String> fields;

    @NotEmpty(message = "At least one template is required")
    @Size(max = 10, message = "Cannot exceed 10 templates")
    @Valid
    @Schema(description = "List of card templates")
    private List<CardTemplateDto> templates;

    @Size(max = 5000, message = "CSS cannot exceed 5000 characters")
    @Schema(description = "CSS styles for cards", example = ".cloze{font-weight:bold;}")
    private String css;
}