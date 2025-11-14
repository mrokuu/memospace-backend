package org.memospace.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response after rebuilding a filtered deck")
public class RebuildFilteredDeckResponse {

    @Schema(description = "Rebuilt filtered deck")
    private FilteredDeckDto filteredDeck;

    @Schema(description = "Total number of cards after rebuild", example = "154")
    private int total;
}
