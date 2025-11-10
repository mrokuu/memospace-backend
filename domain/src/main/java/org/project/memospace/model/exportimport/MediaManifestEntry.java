package org.project.memospace.domain.model.exportimport;

import lombok.Builder;

/**
 * Value object representing a media asset entry in an export manifest.
 * Pure domain model with no framework dependencies.
 *
 * @param id SHA-256 hex
 */
@Builder(toBuilder = true)
public record MediaManifestEntry(String id, String originalFilename, String mimeType, long sizeBytes) {
    public MediaManifestEntry {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Media ID cannot be null or blank");
        }
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("Original filename cannot be null or blank");
        }
        if (mimeType == null || mimeType.isBlank()) {
            throw new IllegalArgumentException("MIME type cannot be null or blank");
        }
        if (sizeBytes < 0) {
            throw new IllegalArgumentException("Size bytes cannot be negative");
        }

    }
}
