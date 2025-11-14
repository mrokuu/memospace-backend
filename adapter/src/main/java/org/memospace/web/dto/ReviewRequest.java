package org.memospace.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to review a card")
public record ReviewRequest(
        @Schema(description = "Quality of the review (0-4)", example = "3")
        @NotNull(message = "Quality cannot be null")
        @Min(value = 0, message = "Quality must be between 0 and 4")
        @Max(value = 4, message = "Quality must be between 0 and 4")
        Integer quality,

        @Schema(description = "Time spent reviewing in milliseconds", example = "5000")
        Integer msSpent
) {}