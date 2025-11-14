package org.memospace.web.dto;

import java.util.List;
import java.util.UUID;

public record MediaDiagnosticsResponse(
        DiagnosticSummaryDto summary,
        List<String> missingOnDisk,
        List<OrphanFileDto> orphansOnDisk,
        List<DanglingReferenceDto> danglingReferences,
        List<String> unusedAssets
) {
    public record DiagnosticSummaryDto(
            int missingOnDisk,
            int orphansOnDisk,
            int danglingReferences,
            int unusedAssets
    ) {
    }

    public record OrphanFileDto(
            String path,
            long sizeBytes
    ) {
    }

    public record DanglingReferenceDto(
            UUID noteId,
            String reference
    ) {
    }
}
