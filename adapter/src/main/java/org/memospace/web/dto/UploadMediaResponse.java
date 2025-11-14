package org.memospace.web.dto;

public record UploadMediaResponse(
        String id,
        String url,
        String mimeType,
        long sizeBytes,
        String originalFilename,
        boolean deduplicated
) {
}
