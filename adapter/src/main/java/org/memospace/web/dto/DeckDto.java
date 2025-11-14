package org.memospace.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Deck information")
public record DeckDto(
        @Schema(description = "Deck ID", example = "1")
        Long id,

        @Schema(description = "Deck name", example = "Spanish Vocabulary")
        String name,

        @Schema(description = "Deck description", example = "Basic Spanish words for beginners")
        String description,

        @Schema(description = "Creation timestamp")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt
) {}