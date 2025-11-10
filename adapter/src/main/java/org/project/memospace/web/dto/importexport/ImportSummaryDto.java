package org.project.memospace.adapter.web.dto.importexport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for import summary counts.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportSummaryDto {
    private CreatedDto created;
    private UpdatedDto updated;
    private SkippedDto skipped;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatedDto {
        private int decks;
        private int noteTypes;
        private int notes;
        private int cards;
        private int mediaLinked;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdatedDto {
        private int notes;
        private int cards;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkippedDto {
        private int duplicateNotes;
    }
}
