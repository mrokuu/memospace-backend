package org.memospace.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Request to update an existing card")
public record UpdateCardRequest(
        @Schema(description = "Front side of the card", example = "What is the Spanish word for 'hello'?")
        @NotBlank(message = "Front cannot be blank")
        @Size(max = 5000, message = "Front cannot exceed 5000 characters")
        String front,

        @Schema(description = "Back side of the card", example = "Hola")
        @NotBlank(message = "Back cannot be blank")
        @Size(max = 5000, message = "Back cannot exceed 5000 characters")
        String back,

        @Schema(description = "Tags associated with the card", example = "[\"greetings\", \"basic\"]")
        List<String> tags
) {}