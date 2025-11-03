package org.project.memospace.application.service.importer;

import org.project.memospace.domain.model.importer.DeduplicationStrategy;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public record TxtImportCommand(
        Supplier<InputStream> inputStreamSupplier,
        long fileSizeBytes,
        String filename,
        String contentType,
        Charset charset,
        Long deckId,
        UUID noteTypeId,
        boolean createMissingNoteType,
        boolean dryRun,
        boolean allowEmptySide,
        DeduplicationStrategy deduplicationStrategy,
        boolean strict,
        boolean force,
        List<String> tags,
        String requestId,
        String userId
) {
}
