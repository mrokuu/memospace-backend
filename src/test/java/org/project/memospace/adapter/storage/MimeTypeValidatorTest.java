package org.project.memospace.adapter.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MimeTypeValidatorTest {

    private MimeTypeValidator validator;

    @BeforeEach
    void setUp() {
        List<String> allowedImages = List.of(
                "image/png",
                "image/jpeg",
                "image/gif",
                "image/webp",
                "image/svg+xml"
        );
        List<String> allowedAudio = List.of(
                "audio/mpeg",
                "audio/mp4",
                "audio/ogg",
                "audio/wav",
                "audio/webm"
        );
        validator = new MimeTypeValidator(allowedImages, allowedAudio);
    }

    @Test
    void shouldAllowValidImageMimeTypes() {
        assertTrue(validator.isAllowed("image/png"));
        assertTrue(validator.isAllowed("image/jpeg"));
        assertTrue(validator.isAllowed("image/gif"));
        assertTrue(validator.isAllowed("image/webp"));
        assertTrue(validator.isAllowed("image/svg+xml"));
    }

    @Test
    void shouldAllowValidAudioMimeTypes() {
        assertTrue(validator.isAllowed("audio/mpeg"));
        assertTrue(validator.isAllowed("audio/mp4"));
        assertTrue(validator.isAllowed("audio/ogg"));
        assertTrue(validator.isAllowed("audio/wav"));
        assertTrue(validator.isAllowed("audio/webm"));
    }

    @Test
    void shouldRejectInvalidMimeTypes() {
        assertFalse(validator.isAllowed("application/pdf"));
        assertFalse(validator.isAllowed("text/html"));
        assertFalse(validator.isAllowed("video/mp4"));
        assertFalse(validator.isAllowed("application/javascript"));
        assertFalse(validator.isAllowed("application/x-executable"));
    }

    @Test
    void shouldRejectDangerousMimeTypes() {
        // Security test: reject potentially dangerous MIME types
        assertFalse(validator.isAllowed("application/x-sh"));
        assertFalse(validator.isAllowed("application/x-executable"));
        assertFalse(validator.isAllowed("application/x-msdownload"));
        assertFalse(validator.isAllowed("text/javascript"));
        assertFalse(validator.isAllowed("application/octet-stream"));
    }

    @Test
    void shouldHandleNullMimeType() {
        assertFalse(validator.isAllowed(null));
    }

    @Test
    void shouldHandleEmptyMimeType() {
        assertFalse(validator.isAllowed(""));
    }

    @Test
    void shouldBeCaseInsensitive() {
        assertTrue(validator.isAllowed("IMAGE/PNG"));
        assertTrue(validator.isAllowed("Image/Png"));
        assertTrue(validator.isAllowed("AUDIO/MPEG"));
        assertTrue(validator.isAllowed("Audio/Mpeg"));
    }

    @Test
    void shouldRejectMalformedMimeTypes() {
        assertFalse(validator.isAllowed("not-a-mime-type"));
        assertFalse(validator.isAllowed("image"));
        assertFalse(validator.isAllowed("/png"));
        assertFalse(validator.isAllowed("image/"));
    }

    @Test
    void shouldGetCorrectExtensionForImageMimeTypes() {
        assertEquals("png", validator.getExtensionForMimeType("image/png"));
        assertEquals("jpg", validator.getExtensionForMimeType("image/jpeg"));
        assertEquals("jpg", validator.getExtensionForMimeType("image/jpg"));
        assertEquals("gif", validator.getExtensionForMimeType("image/gif"));
        assertEquals("webp", validator.getExtensionForMimeType("image/webp"));
        assertEquals("svg", validator.getExtensionForMimeType("image/svg+xml"));
    }

    @Test
    void shouldGetCorrectExtensionForAudioMimeTypes() {
        assertEquals("mp3", validator.getExtensionForMimeType("audio/mpeg"));
        assertEquals("mp3", validator.getExtensionForMimeType("audio/mp3"));
        assertEquals("m4a", validator.getExtensionForMimeType("audio/mp4"));
        assertEquals("ogg", validator.getExtensionForMimeType("audio/ogg"));
        assertEquals("wav", validator.getExtensionForMimeType("audio/wav"));
        assertEquals("wav", validator.getExtensionForMimeType("audio/x-wav"));
        assertEquals("webm", validator.getExtensionForMimeType("audio/webm"));
    }

    @Test
    void shouldReturnBinForUnknownMimeType() {
        assertEquals("bin", validator.getExtensionForMimeType("application/pdf"));
        assertEquals("bin", validator.getExtensionForMimeType("unknown/type"));
        assertEquals("bin", validator.getExtensionForMimeType(""));
    }

    @Test
    void shouldHandleCaseInsensitiveExtensionLookup() {
        assertEquals("png", validator.getExtensionForMimeType("IMAGE/PNG"));
        assertEquals("jpg", validator.getExtensionForMimeType("Image/Jpeg"));
        assertEquals("mp3", validator.getExtensionForMimeType("AUDIO/MPEG"));
    }

    @Test
    void shouldReturnUnmodifiableAllowedMimeTypesSet() {
        Set<String> allowedTypes = validator.getAllowedMimeTypes();
        assertNotNull(allowedTypes);
        assertFalse(allowedTypes.isEmpty());

        // Should throw UnsupportedOperationException
        assertThrows(UnsupportedOperationException.class, () ->
                allowedTypes.add("image/bmp")
        );
    }

    @Test
    void shouldContainAllConfiguredMimeTypes() {
        Set<String> allowedTypes = validator.getAllowedMimeTypes();

        // Images
        assertTrue(allowedTypes.contains("image/png"));
        assertTrue(allowedTypes.contains("image/jpeg"));
        assertTrue(allowedTypes.contains("image/gif"));
        assertTrue(allowedTypes.contains("image/webp"));
        assertTrue(allowedTypes.contains("image/svg+xml"));

        // Audio
        assertTrue(allowedTypes.contains("audio/mpeg"));
        assertTrue(allowedTypes.contains("audio/mp4"));
        assertTrue(allowedTypes.contains("audio/ogg"));
        assertTrue(allowedTypes.contains("audio/wav"));
        assertTrue(allowedTypes.contains("audio/webm"));
    }

    @Test
    void shouldWorkWithEmptyAllowedLists() {
        MimeTypeValidator emptyValidator = new MimeTypeValidator(List.of(), List.of());

        assertFalse(emptyValidator.isAllowed("image/png"));
        assertFalse(emptyValidator.isAllowed("audio/mpeg"));
        // Extension mapping is independent of allowed types - returns hardcoded mapping
        assertEquals("png", emptyValidator.getExtensionForMimeType("image/png"));
        assertEquals("mp3", emptyValidator.getExtensionForMimeType("audio/mpeg"));
        assertEquals("bin", emptyValidator.getExtensionForMimeType("unknown/type"));
        assertTrue(emptyValidator.getAllowedMimeTypes().isEmpty());
    }

    @Test
    void shouldWorkWithOnlyImages() {
        MimeTypeValidator imageOnlyValidator = new MimeTypeValidator(
                List.of("image/png", "image/jpeg"),
                List.of()
        );

        assertTrue(imageOnlyValidator.isAllowed("image/png"));
        assertTrue(imageOnlyValidator.isAllowed("image/jpeg"));
        assertFalse(imageOnlyValidator.isAllowed("audio/mpeg"));
    }

    @Test
    void shouldWorkWithOnlyAudio() {
        MimeTypeValidator audioOnlyValidator = new MimeTypeValidator(
                List.of(),
                List.of("audio/mpeg", "audio/wav")
        );

        assertTrue(audioOnlyValidator.isAllowed("audio/mpeg"));
        assertTrue(audioOnlyValidator.isAllowed("audio/wav"));
        assertFalse(audioOnlyValidator.isAllowed("image/png"));
    }

    @Test
    void shouldHandleDuplicatesInConfiguration() {
        MimeTypeValidator validatorWithDuplicates = new MimeTypeValidator(
                List.of("image/png", "image/png", "image/jpeg"),
                List.of("audio/mpeg", "audio/mpeg")
        );

        assertTrue(validatorWithDuplicates.isAllowed("image/png"));
        assertTrue(validatorWithDuplicates.isAllowed("image/jpeg"));
        assertTrue(validatorWithDuplicates.isAllowed("audio/mpeg"));
    }

    @Test
    void shouldRejectPartialMimeTypeMatches() {
        assertFalse(validator.isAllowed("image/png-extra"));
        assertFalse(validator.isAllowed("ximage/png"));
        assertFalse(validator.isAllowed("audio/mpeg2"));
    }

    @Test
    void shouldHandleWhitespaceInMimeType() {
        assertFalse(validator.isAllowed("image/png "));
        assertFalse(validator.isAllowed(" image/png"));
        assertFalse(validator.isAllowed("image/ png"));
    }

    @Test
    void shouldDistinguishSimilarMimeTypes() {
        // Both audio/mp3 and audio/mpeg might map to .mp3, but are different MIME types
        // Configure validator with only one
        MimeTypeValidator strictValidator = new MimeTypeValidator(
                List.of(),
                List.of("audio/mpeg")  // Only allow audio/mpeg, not audio/mp3
        );

        assertTrue(strictValidator.isAllowed("audio/mpeg"));
        assertFalse(strictValidator.isAllowed("audio/mp3"));
    }

    @Test
    void shouldPreventMimeTypeSpoofing() {
        // Security test: should not allow MIME types with special characters
        assertFalse(validator.isAllowed("image/png;charset=utf-8"));
        assertFalse(validator.isAllowed("image/png\0"));
        assertFalse(validator.isAllowed("image/png\n"));
    }
}
