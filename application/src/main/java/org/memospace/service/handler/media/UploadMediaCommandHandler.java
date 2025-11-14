package org.memospace.service.handler.media;

import lombok.RequiredArgsConstructor;
import org.memospace.service.CommandHandler;
import org.memospace.service.command.media.UploadMediaCommand;
import org.memospace.exception.InvalidMediaException;
import org.memospace.model.MediaAsset;
import org.memospace.model.MediaId;
import org.memospace.port.MediaRepositoryPort;
import org.memospace.port.MediaStoragePort;
import org.memospace.service.FilenameSanitizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.Clock;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class UploadMediaCommandHandler implements CommandHandler<UploadMediaCommand, UploadMediaCommandHandler.UploadResult> {

    private static final Logger log = LoggerFactory.getLogger(UploadMediaCommandHandler.class);

    private final MediaStoragePort storagePort;
    private final MediaRepositoryPort repositoryPort;
    private final FilenameSanitizer filenameSanitizer;
    private final Clock clock;

    @Override
    @Transactional
    public UploadResult handle(UploadMediaCommand command) {
        InputStream data = command.data();
        String originalFilename = command.originalFilename();
        String mimeType = command.mimeType();

        // Validate inputs
        if (data == null) {
            throw new InvalidMediaException("File data cannot be null");
        }
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new InvalidMediaException("Filename cannot be null or blank");
        }
        if (mimeType == null || mimeType.isBlank()) {
            throw new InvalidMediaException("MIME type cannot be null or blank");
        }

        // Sanitize filename
        String sanitizedFilename = filenameSanitizer.sanitize(originalFilename);

        // Save to storage (computes hash, handles deduplication)
        MediaStoragePort.SaveResult saveResult = storagePort.save(data, sanitizedFilename, mimeType);
        MediaId mediaId = saveResult.getId();
        boolean deduplicated = saveResult.isDeduplicated();

        // Check if metadata already exists
        MediaAsset existingAsset = repositoryPort.findById(mediaId).orElse(null);

        MediaAsset asset;
        if (existingAsset != null) {
            // Update metadata with new filename (keep first upload's timestamp)
            asset = new MediaAsset(
                    mediaId,
                    sanitizedFilename,
                    mimeType,
                    extractExtension(sanitizedFilename),
                    saveResult.getSizeBytes(),
                    existingAsset.getCreatedAt(),
                    existingAsset.getUsageCount()
            );
        } else {
            // Create new metadata
            Instant now = clock.instant();
            asset = new MediaAsset(
                    mediaId,
                    sanitizedFilename,
                    mimeType,
                    extractExtension(sanitizedFilename),
                    saveResult.getSizeBytes(),
                    now,
                    0
            );
        }

        // Upsert metadata
        MediaAsset savedAsset = repositoryPort.upsert(asset);

        log.info("Media uploaded: {} (deduplicated: {})", mediaId, deduplicated);

        return new UploadResult(savedAsset, deduplicated);
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "bin";
        }
        int lastDot = filename.lastIndexOf('.');
        if (lastDot < filename.length() - 1) {
            return filename.substring(lastDot + 1).toLowerCase();
        }
        return "bin";
    }

    public static class UploadResult {
        private final MediaAsset asset;
        private final boolean deduplicated;

        public UploadResult(MediaAsset asset, boolean deduplicated) {
            this.asset = asset;
            this.deduplicated = deduplicated;
        }

        public MediaAsset getAsset() {
            return asset;
        }

        public boolean isDeduplicated() {
            return deduplicated;
        }
    }
}
