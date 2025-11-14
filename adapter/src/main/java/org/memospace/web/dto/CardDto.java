package org.memospace.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Card information")
public record CardDto(
        @Schema(description = "Card ID", example = "1")
        Long id,

        @Schema(description = "Deck ID this card belongs to", example = "1")
        Long deckId,

        @Schema(description = "Front side of the card", example = "What is the Spanish word for 'hello'?")
        String front,

        @Schema(description = "Back side of the card", example = "Hola")
        String back,

        @Schema(description = "Tags associated with the card", example = "[\"greetings\", \"basic\"]")
        List<String> tags,

        @Schema(description = "Ease factor for spaced repetition", example = "2.5")
        double easeFactor,

        @Schema(description = "Interval in days", example = "1")
        int intervalDays,

        @Schema(description = "Number of repetitions", example = "0")
        int repetitions,

        @Schema(description = "When the card is due for review")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime dueAt,

        @Schema(description = "Creation timestamp")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,

        @Schema(description = "Last update timestamp")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime updatedAt,

        @Schema(description = "Note ID this card was generated from", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID noteId,

        @Schema(description = "Template ID used to generate this card", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID templateId,

        @Schema(description = "Cloze index for cloze deletion cards", example = "1")
        Integer clozeIndex
) {}