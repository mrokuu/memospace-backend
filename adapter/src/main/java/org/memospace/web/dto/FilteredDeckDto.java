package org.memospace.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.memospace.model.FilteredMode;
import org.memospace.model.ReturnPolicy;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Filtered deck data transfer object")
public record FilteredDeckDto(
        @Schema(description = "Filtered deck ID")
        UUID id,

        @Schema(description = "Filtered deck name")
        String name,

        @Schema(description = "Owner deck ID")
        UUID ownerDeckId,

        @Schema(description = "Query string")
        String query,

        @Schema(description = "Maximum number of cards")
        int limit,

        @Schema(description = "Mode (REVIEW_ONLY or INCLUDE_NEW)")
        FilteredMode mode,

        @Schema(description = "Return policy")
        ReturnPolicy returnPolicy,

        @Schema(description = "Creation timestamp")
        Instant createdAt,

        @Schema(description = "Expiration timestamp")
        Instant expiresAt,

        @Schema(description = "Last built timestamp")
        Instant lastBuiltAt
) {}
