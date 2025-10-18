package org.project.memospace.domain.port;

import org.project.memospace.domain.model.MediaId;

import java.io.InputStream;
import java.util.List;

/**
 * Port for content-addressed media storage.
 * Implemented by filesystem adapter with SHA-256 based storage.
 */
public interface MediaStoragePort {

    /**
     * Saves media content to storage with content-addressing (SHA-256).
     * If content already exists (same hash), returns existing ID without rewriting.
     *
     * @param data input stream of media content
     * @param suggestedFilename original filename (for extension extraction)
     * @param mimeType MIME type of the content
     * @return MediaId (SHA-256 hash) and whether content was deduplicated
     */
    SaveResult save(InputStream data, String suggestedFilename, String mimeType);

    /**
     * Retrieves media content by ID.
     *
     * @param id media identifier (SHA-256 hash)
     * @return input stream of media content
     * @throws org.project.memospace.domain.exception.MediaNotFoundException if not found
     */
    InputStream get(MediaId id);

    /**
     * Checks if media exists in storage.
     *
     * @param id media identifier
     * @return true if exists, false otherwise
     */
    boolean exists(MediaId id);

    /**
     * Lists all media IDs present in storage.
     * Used for diagnostics to find orphans.
     *
     * @return list of all media IDs on disk
     */
    List<MediaId> listAll();

    /**
     * Gets the size of media content in bytes.
     *
     * @param id media identifier
     * @return size in bytes
     * @throws org.project.memospace.domain.exception.MediaNotFoundException if not found
     */
    long size(MediaId id);

    /**
     * Result of save operation including deduplication status.
     */
    class SaveResult {
        private final MediaId id;
        private final boolean deduplicated;
        private final long sizeBytes;

        public SaveResult(MediaId id, boolean deduplicated, long sizeBytes) {
            this.id = id;
            this.deduplicated = deduplicated;
            this.sizeBytes = sizeBytes;
        }

        public MediaId getId() {
            return id;
        }

        public boolean isDeduplicated() {
            return deduplicated;
        }

        public long getSizeBytes() {
            return sizeBytes;
        }
    }
}
