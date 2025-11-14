package org.memospace.service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pure domain service to extract media references from note field values.
 * Supports:
 * - Markdown: ![alt](/media/{id}), ![alt](media/{id})
 * - HTML: <img src="/media/{id}">, <audio src="/media/{id}">
 * - Anki-style: [sound:filename.ext], sound:[filename.ext]
 * - Direct references: /media/{id}
 */
public class MediaReferenceExtractor {

    // Markdown: ![...](/media/{id}) or ![...](media/{id})
    private static final Pattern MARKDOWN_PATTERN =
            Pattern.compile("!\\[[^\\]]*\\]\\(/?media/([a-f0-9]{64})[^)]*\\)");

    // HTML img/audio: <img src="/media/{id}"> or <audio src="media/{id}">
    private static final Pattern HTML_IMG_AUDIO_PATTERN =
            Pattern.compile("<(?:img|audio)[^>]+src=[\"']/?media/([a-f0-9]{64})[\"'][^>]*>");

    // Direct media path: /media/{id} or media/{id}
    private static final Pattern DIRECT_MEDIA_PATTERN =
            Pattern.compile("/?media/([a-f0-9]{64})");

    // Anki-style sound: [sound:filename] or sound:[filename]
    private static final Pattern SOUND_BRACKET_PATTERN =
            Pattern.compile("\\[sound:([^\\]]+)\\]");

    private static final Pattern SOUND_COLON_PATTERN =
            Pattern.compile("sound:\\[([^\\]]+)\\]");

    /**
     * Extracts all media references from note field values.
     * Returns a set of media IDs (64-char hex) and filenames.
     *
     * @param fieldValues map of field names to their content
     * @return deduplicated set of references (IDs or filenames)
     */
    public Set<String> extract(Map<String, String> fieldValues) {
        if (fieldValues == null || fieldValues.isEmpty()) {
            return Set.of();
        }

        Set<String> references = new HashSet<>();

        for (String content : fieldValues.values()) {
            if (content == null || content.isBlank()) {
                continue;
            }

            extractFromContent(content, references);
        }

        return references;
    }

    private void extractFromContent(String content, Set<String> references) {
        // Extract markdown image references
        extractPattern(MARKDOWN_PATTERN, content, references);

        // Extract HTML img/audio references
        extractPattern(HTML_IMG_AUDIO_PATTERN, content, references);

        // Extract direct media paths
        extractPattern(DIRECT_MEDIA_PATTERN, content, references);

        // Extract Anki-style sound references [sound:filename]
        extractPattern(SOUND_BRACKET_PATTERN, content, references);

        // Extract Anki-style sound references sound:[filename]
        extractPattern(SOUND_COLON_PATTERN, content, references);
    }

    private void extractPattern(Pattern pattern, String content, Set<String> references) {
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String reference = matcher.group(1).trim();
            if (!reference.isEmpty()) {
                references.add(reference);
            }
        }
    }
}
