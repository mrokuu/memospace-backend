package org.memospace.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.With;

import java.time.Instant;

/**
 * Domain entity representing a media asset (image, audio).
 * Pure domain model with no framework dependencies.
 */
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder(toBuilder = true)
public class MediaAsset {
    @With
    @EqualsAndHashCode.Include
    MediaId id;
    String originalFilename;
    String mimeType;
    String extension;
    long sizeBytes;
    Instant createdAt;
    int usageCount;

    public MediaAsset(
            MediaId id,
            String originalFilename,
            String mimeType,
            String extension,
            long sizeBytes,
            Instant createdAt,
            int usageCount) {
        if (id == null) {
            throw new IllegalArgumentException("MediaId cannot be null");
        }
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("Original filename cannot be null or blank");
        }
        if (mimeType == null || mimeType.isBlank()) {
            throw new IllegalArgumentException("MIME type cannot be null or blank");
        }
        if (extension == null || extension.isBlank()) {
            throw new IllegalArgumentException("Extension cannot be null or blank");
        }
        if (sizeBytes < 0) {
            throw new IllegalArgumentException("Size bytes cannot be negative");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Created at cannot be null");
        }
        if (usageCount < 0) {
            throw new IllegalArgumentException("Usage count cannot be negative");
        }

        this.id = id;
        this.originalFilename = originalFilename;
        this.mimeType = mimeType;
        this.extension = extension;
        this.sizeBytes = sizeBytes;
        this.createdAt = createdAt;
        this.usageCount = usageCount;
    }

    public MediaAsset withUsageCount(int newUsageCount) {
        return toBuilder().usageCount(newUsageCount).build();
    }

    public MediaAsset incrementUsage(int delta) {
        return withUsageCount(usageCount + delta);
    }
}
