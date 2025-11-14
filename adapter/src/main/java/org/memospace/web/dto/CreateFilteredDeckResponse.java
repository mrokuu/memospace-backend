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
@Schema(description = "Response after creating a filtered deck")
public class CreateFilteredDeckResponse {

    @Schema(description = "Created filtered deck")
    private FilteredDeckDto filteredDeck;

    @Schema(description = "Total number of cards in the filtered deck", example = "176")
    private int total;
}
