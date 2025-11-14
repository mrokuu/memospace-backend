package org.memospace.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Card template for generating cards from notes")
public class CardTemplateDto {

    @Schema(description = "Template ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @NotBlank(message = "Template name is required")
    @Schema(description = "Template name", example = "Card 1")
    private String name;

    @NotBlank(message = "Front template is required")
    @Schema(description = "Front template with field placeholders", example = "{{Front}}")
    private String frontTemplate;

    @NotBlank(message = "Back template is required")
    @Schema(description = "Back template with field placeholders", example = "{{Back}}")
    private String backTemplate;

    @NotNull(message = "isCloze field is required")
    @Schema(description = "Whether this template generates cloze cards", example = "false")
    private Boolean isCloze;
}