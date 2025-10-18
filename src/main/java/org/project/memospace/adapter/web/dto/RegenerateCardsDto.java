package org.project.memospace.adapter.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Result of regenerating cards from a note")
public record RegenerateCardsDto(
        @Schema(description = "Number of cards generated", example = "2")
        int cardCount,

        @Schema(description = "Regeneration mode", example = "UPDATED")
        RegenerateMode mode) {
}
