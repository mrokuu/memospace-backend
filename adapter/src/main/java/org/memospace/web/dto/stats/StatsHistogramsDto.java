package org.memospace.web.dto.stats;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Histogram statistics response")
public record StatsHistogramsDto(
        @Schema(description = "Deck ID or 'all'")
        String deckId,

        @Schema(description = "Window period", example = "90d")
        String window,

        @Schema(description = "Interval histogram")
        HistogramDto intervals,

        @Schema(description = "Ease factor histogram")
        HistogramDto ease,

        @Schema(description = "Answer button totals")
        AnswerButtonsDto answerButtons,

        @Schema(description = "Due forecast for upcoming days")
        List<DuePointDto> dueForecast
) {
    public record HistogramDto(
            List<BinDto> bins,
            int total
    ) {}

    public record BinDto(
            String label,
            Double min,
            Double max,
            int count
    ) {}

    public record AnswerButtonsDto(
            int again,
            int hard,
            int good,
            int easy
    ) {}

    public record DuePointDto(LocalDate date, int due) {}
}
