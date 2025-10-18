package org.project.memospace.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MediaReferenceExtractorTest {

    private MediaReferenceExtractor extractor;
    private static final String VALID_ID = "a".repeat(64);
    private static final String VALID_ID_2 = "b".repeat(64);

    @BeforeEach
    void setUp() {
        extractor = new MediaReferenceExtractor();
    }

    @Test
    void shouldReturnEmptySetForNullOrEmptyFields() {
        assertTrue(extractor.extract(null).isEmpty());
        assertTrue(extractor.extract(Map.of()).isEmpty());
    }

    @Test
    void shouldExtractMarkdownImageReferences() {
        Map<String, String> fields = Map.of(
                "Front", "Look at this: ![alt](/media/" + VALID_ID + ")"
        );
        Set<String> refs = extractor.extract(fields);
        assertEquals(1, refs.size());
        assertTrue(refs.contains(VALID_ID));
    }

    @Test
    void shouldExtractMarkdownImageWithoutLeadingSlash() {
        Map<String, String> fields = Map.of(
                "Front", "![alt](media/" + VALID_ID + ")"
        );
        Set<String> refs = extractor.extract(fields);
        assertTrue(refs.contains(VALID_ID));
    }

    @Test
    void shouldExtractHtmlImageReferences() {
        Map<String, String> fields = Map.of(
                "Front", "<img src=\"/media/" + VALID_ID + "\" alt=\"test\">"
        );
        Set<String> refs = extractor.extract(fields);
        assertTrue(refs.contains(VALID_ID));
    }

    @Test
    void shouldExtractHtmlAudioReferences() {
        Map<String, String> fields = Map.of(
                "Front", "<audio src=\"/media/" + VALID_ID + "\"></audio>"
        );
        Set<String> refs = extractor.extract(fields);
        assertTrue(refs.contains(VALID_ID));
    }

    @Test
    void shouldExtractDirectMediaPaths() {
        Map<String, String> fields = Map.of(
                "Front", "Check /media/" + VALID_ID
        );
        Set<String> refs = extractor.extract(fields);
        assertTrue(refs.contains(VALID_ID));
    }

    @Test
    void shouldExtractAnkiStyleSoundBracket() {
        Map<String, String> fields = Map.of(
                "Front", "Listen: [sound:hello.mp3]"
        );
        Set<String> refs = extractor.extract(fields);
        assertTrue(refs.contains("hello.mp3"));
    }

    @Test
    void shouldExtractAnkiStyleSoundColon() {
        Map<String, String> fields = Map.of(
                "Front", "Listen: sound:[goodbye.mp3]"
        );
        Set<String> refs = extractor.extract(fields);
        assertTrue(refs.contains("goodbye.mp3"));
    }

    @Test
    void shouldExtractMultipleReferences() {
        Map<String, String> fields = Map.of(
                "Front", "![img1](/media/" + VALID_ID + ") and [sound:audio.mp3]",
                "Back", "<img src=\"/media/" + VALID_ID_2 + "\">"
        );
        Set<String> refs = extractor.extract(fields);
        assertEquals(3, refs.size());
        assertTrue(refs.contains(VALID_ID));
        assertTrue(refs.contains(VALID_ID_2));
        assertTrue(refs.contains("audio.mp3"));
    }

    @Test
    void shouldDeduplicateReferences() {
        Map<String, String> fields = Map.of(
                "Front", "![](/media/" + VALID_ID + ") ![](/media/" + VALID_ID + ")"
        );
        Set<String> refs = extractor.extract(fields);
        assertEquals(1, refs.size());
        assertTrue(refs.contains(VALID_ID));
    }

    @Test
    void shouldHandleMixedContent() {
        String content = "Here's an image: ![test](/media/" + VALID_ID + ") " +
                "and HTML <img src=\"/media/" + VALID_ID_2 + "\"> " +
                "plus audio [sound:test.mp3] " +
                "and direct link /media/" + VALID_ID;

        Map<String, String> fields = Map.of("Content", content);
        Set<String> refs = extractor.extract(fields);

        assertEquals(3, refs.size());
        assertTrue(refs.contains(VALID_ID));
        assertTrue(refs.contains(VALID_ID_2));
        assertTrue(refs.contains("test.mp3"));
    }

    @Test
    void shouldIgnoreMalformedReferences() {
        Map<String, String> fields = Map.of(
                "Front", "![](/media/tooshort) [sound:] sound:[]"
        );
        Set<String> refs = extractor.extract(fields);
        assertTrue(refs.isEmpty());
    }

    @Test
    void shouldHandleEmptyFields() {
        Map<String, String> fields = Map.of(
                "Front", "",
                "Back", "   ",
                "Extra", "actual content with ![](/media/" + VALID_ID + ")"
        );
        Set<String> refs = extractor.extract(fields);
        assertEquals(1, refs.size());
        assertTrue(refs.contains(VALID_ID));
    }

    @Test
    void shouldHandleNullFieldValues() {
        Map<String, String> fields = Map.of(
                "Front", "![](/media/" + VALID_ID + ")"
        );
        // Add null value - using mutable map
        java.util.HashMap<String, String> mutableFields = new java.util.HashMap<>(fields);
        mutableFields.put("Back", null);

        Set<String> refs = extractor.extract(mutableFields);
        assertEquals(1, refs.size());
        assertTrue(refs.contains(VALID_ID));
    }

    @Test
    void shouldExtractComplexAnkiSoundFilenames() {
        Map<String, String> fields = Map.of(
                "Front", "[sound:my-audio_file.2023.mp3]"
        );
        Set<String> refs = extractor.extract(fields);
        assertTrue(refs.contains("my-audio_file.2023.mp3"));
    }
}
