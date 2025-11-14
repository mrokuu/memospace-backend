package org.memospace.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to create a new deck")
public record CreateDeckRequest(
        @Schema(description = "Deck name", example = "Spanish Vocabulary")
        @NotBlank(message = "Name cannot be blank")
        @Size(max = 255, message = "Name cannot exceed 255 characters")
        String name,

        @Schema(description = "Deck description", example = "Basic Spanish words for beginners")
        @Size(max = 1000, message = "Description cannot exceed 1000 characters")
        String description
) {}