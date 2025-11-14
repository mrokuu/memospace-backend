package org.memospace.storage;

import java.util.*;

/**
 * Validates MIME types against allowlist and provides extension mappings.
 */
public class MimeTypeValidator {

    private final Set<String> allowedMimeTypes;
    private final Map<String, String> mimeToExtension;

    public MimeTypeValidator(List<String> allowedImages, List<String> allowedAudio) {
        this.allowedMimeTypes = new HashSet<>();
        this.allowedMimeTypes.addAll(allowedImages);
        this.allowedMimeTypes.addAll(allowedAudio);

        this.mimeToExtension = buildMimeToExtensionMap();
    }

    public boolean isAllowed(String mimeType) {
        return mimeType != null && allowedMimeTypes.contains(mimeType.toLowerCase());
    }

    public String getExtensionForMimeType(String mimeType) {
        return mimeToExtension.getOrDefault(mimeType.toLowerCase(), "bin");
    }

    public Set<String> getAllowedMimeTypes() {
        return Collections.unmodifiableSet(allowedMimeTypes);
    }

    private Map<String, String> buildMimeToExtensionMap() {
        Map<String, String> map = new HashMap<>();

        // Images
        map.put("image/png", "png");
        map.put("image/jpeg", "jpg");
        map.put("image/jpg", "jpg");
        map.put("image/gif", "gif");
        map.put("image/webp", "webp");
        map.put("image/svg+xml", "svg");

        // Audio
        map.put("audio/mpeg", "mp3");
        map.put("audio/mp3", "mp3");
        map.put("audio/mp4", "m4a");
        map.put("audio/ogg", "ogg");
        map.put("audio/wav", "wav");
        map.put("audio/x-wav", "wav");
        map.put("audio/webm", "webm");

        return map;
    }
}
