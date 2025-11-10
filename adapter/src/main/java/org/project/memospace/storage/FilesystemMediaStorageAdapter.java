package org.project.memospace.adapter.storage;

import org.project.memospace.domain.exception.InvalidMediaException;
import org.project.memospace.domain.exception.MediaNotFoundException;
import org.project.memospace.domain.model.MediaId;
import org.project.memospace.domain.port.MediaStoragePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Filesystem-based implementation of MediaStoragePort.
 * Uses SHA-256 content-addressing with path layout: {root}/{hash[0..2]}/{hash}.{ext}
 */
public class FilesystemMediaStorageAdapter implements MediaStoragePort {

    private static final Logger log = LoggerFactory.getLogger(FilesystemMediaStorageAdapter.class);
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int BUFFER_SIZE = 8192; // 8KB buffer for file I/O

    private final Path rootPath;
    private final MimeTypeValidator mimeTypeValidator;
    private final long maxSizeBytes;

    public FilesystemMediaStorageAdapter(Path rootPath, MimeTypeValidator mimeTypeValidator, long maxSizeBytes) {
        this.rootPath = rootPath;
        this.mimeTypeValidator = mimeTypeValidator;
        this.maxSizeBytes = maxSizeBytes;
        ensureRootExists();
    }

    private void ensureRootExists() {
        try {
            Files.createDirectories(rootPath);
        } catch (IOException e) {
            throw new InvalidMediaException("Failed to create media storage root directory", e);
        }
    }

    @Override
    public SaveResult save(InputStream data, String suggestedFilename, String mimeType) {
        validateMimeType(mimeType);
        String extension = extractExtension(suggestedFilename, mimeType);

        try {
            // Create temp file to compute hash while reading
            Path tempFile = Files.createTempFile(rootPath, "upload-", ".tmp");

            try {
                // Copy to temp file and compute hash
                MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
                long size;

                try (DigestInputStream dis = new DigestInputStream(data, digest);
                     OutputStream out = Files.newOutputStream(tempFile)) {

                    size = copyWithSizeLimit(dis, out, maxSizeBytes);
                }

                // Get hash
                String hashHex = bytesToHex(digest.digest());
                MediaId mediaId = MediaId.of(hashHex);

                // Check if file already exists
                Path targetPath = buildPath(mediaId, extension);
                boolean deduplicated = Files.exists(targetPath);

                if (!deduplicated) {
                    // Move temp file to final location
                    Files.createDirectories(targetPath.getParent());
                    Files.move(tempFile, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    log.info("Saved new media file: {} ({} bytes)", mediaId, size);
                } else {
                    // Delete temp file, content already exists
                    Files.deleteIfExists(tempFile);
                    log.info("Media file already exists (deduplicated): {}", mediaId);
                }

                return new SaveResult(mediaId, deduplicated, size);

            } finally {
                // Cleanup temp file if still exists
                Files.deleteIfExists(tempFile);
            }

        } catch (NoSuchAlgorithmException e) {
            throw new InvalidMediaException("Hash algorithm not available", e);
        } catch (IOException e) {
            throw new InvalidMediaException("Failed to save media file", e);
        }
    }

    private long copyWithSizeLimit(InputStream in, OutputStream out, long maxSize) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        long total = 0;
        int read;

        while ((read = in.read(buffer)) != -1) {
            total += read;
            if (total > maxSize) {
                throw new InvalidMediaException("File size exceeds maximum allowed: " + maxSize + " bytes");
            }
            out.write(buffer, 0, read);
        }

        if (total == 0) {
            throw new InvalidMediaException("Empty file not allowed");
        }

        return total;
    }

    @Override
    public InputStream get(MediaId id) {
        Path path = findExistingPath(id);
        if (path == null) {
            throw new MediaNotFoundException("Media not found: " + id);
        }

        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new MediaNotFoundException("Failed to read media file: " + id, e);
        }
    }

    @Override
    public boolean exists(MediaId id) {
        return findExistingPath(id) != null;
    }

    @Override
    public List<MediaId> listAll() {
        List<MediaId> result = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(rootPath, 2)) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> !p.getFileName().toString().endsWith(".tmp"))
                    .forEach(path -> {
                        String filename = path.getFileName().toString();
                        int dotIndex = filename.lastIndexOf('.');
                        if (dotIndex > 0) {
                            String hash = filename.substring(0, dotIndex);
                            if (hash.matches("^[a-f0-9]{64}$")) {
                                result.add(MediaId.of(hash));
                            }
                        }
                    });
        } catch (IOException e) {
            log.error("Failed to list media files", e);
        }

        return result;
    }

    @Override
    public long size(MediaId id) {
        Path path = findExistingPath(id);
        if (path == null) {
            throw new MediaNotFoundException("Media not found: " + id);
        }

        try {
            return Files.size(path);
        } catch (IOException e) {
            throw new MediaNotFoundException("Failed to get size of media file: " + id, e);
        }
    }

    /**
     * Finds the actual file path for a media ID, checking all possible extensions.
     */
    private Path findExistingPath(MediaId id) {
        String hash = id.value();
        Path dir = rootPath.resolve(hash.substring(0, 2));

        if (!Files.exists(dir)) {
            return null;
        }

        try (Stream<Path> paths = Files.list(dir)) {
            return paths
                    .filter(p -> p.getFileName().toString().startsWith(hash + "."))
                    .findFirst()
                    .orElse(null);
        } catch (IOException e) {
            log.error("Failed to search for media file: {}", id, e);
            return null;
        }
    }

    private Path buildPath(MediaId id, String extension) {
        String hash = id.value();
        String prefix = hash.substring(0, 2);
        return rootPath.resolve(prefix).resolve(hash + "." + extension);
    }

    private void validateMimeType(String mimeType) {
        if (!mimeTypeValidator.isAllowed(mimeType)) {
            throw new InvalidMediaException("Unsupported media type: " + mimeType);
        }
    }

    private String extractExtension(String filename, String mimeType) {
        // Try to extract from filename first
        if (filename != null && filename.contains(".")) {
            int lastDot = filename.lastIndexOf('.');
            if (lastDot < filename.length() - 1) {
                String ext = filename.substring(lastDot + 1).toLowerCase();
                if (ext.matches("^[a-z0-9]{1,10}$")) {
                    return ext;
                }
            }
        }

        // Fallback to MIME type mapping
        return mimeTypeValidator.getExtensionForMimeType(mimeType);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Package-visible for testing - returns actual file path if exists.
     */
    Path getPhysicalPath(MediaId id) {
        return findExistingPath(id);
    }
}
