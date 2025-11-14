package org.memospace.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.memospace.model.FilteredMode;
import org.memospace.model.ReturnPolicy;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Request to create a new filtered deck")
public record CreateFilteredDeckRequest(
        @Schema(description = "Filtered deck name", example = "Cram: verbs this week")
        @Size(max = 255, message = "Name cannot exceed 255 characters")
        String name,

        @Schema(description = "Owner deck ID")
        UUID ownerDeckId,

        @Schema(description = "Query string (M3 browser language)", example = "tag:verbs added:-7d")
        @NotBlank(message = "Query cannot be blank")
        String query,

        @Schema(description = "Maximum number of cards to include", example = "200")
        @NotNull(message = "Limit cannot be null")
        @Min(value = 1, message = "Limit must be at least 1")
        @Max(value = 10000, message = "Limit cannot exceed 10000")
        Integer limit,

        @Schema(description = "Mode (REVIEW_ONLY or INCLUDE_NEW)", example = "INCLUDE_NEW")
        @NotNull(message = "Mode cannot be null")
        FilteredMode mode,

        @Schema(description = "Return policy (WHEN_EMPTY, ON_DELETE, AT_CUTOFF)", example = "WHEN_EMPTY")
        @NotNull(message = "Return policy cannot be null")
        ReturnPolicy returnPolicy,

        @Schema(description = "Expiration timestamp (optional)")
        Instant expiresAt
) {}
