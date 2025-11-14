package org.memospace.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Review log entry")
public record ReviewLogDto(
        @Schema(description = "Review log ID", example = "1")
        Long id,

        @Schema(description = "Card ID that was reviewed", example = "1")
        Long cardId,

        @Schema(description = "Quality of the review (0-4)", example = "3")
        int quality,

        @Schema(description = "When the review was performed")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime reviewedAt,

        @Schema(description = "Time spent reviewing in milliseconds", example = "5000")
        Integer msSpent
) {}