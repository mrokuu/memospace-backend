package org.memospace.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to update an existing deck")
public record UpdateDeckRequest(
        @Schema(description = "Deck name", example = "Spanish Vocabulary - Advanced")
        @NotBlank(message = "Name cannot be blank")
        @Size(max = 255, message = "Name cannot exceed 255 characters")
        String name,

        @Schema(description = "Deck description", example = "Advanced Spanish words and phrases")
        @Size(max = 1000, message = "Description cannot exceed 1000 characters")
        String description
) {}