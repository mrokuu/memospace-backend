package org.project.memospace.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FilenameSanitizerTest {

    private FilenameSanitizer sanitizer;

    @BeforeEach
    void setUp() {
        sanitizer = new FilenameSanitizer();
    }

    @Test
    void shouldKeepValidFilenames() {
        assertEquals("test.png", sanitizer.sanitize("test.png"));
        assertEquals("my_file-2023.jpg", sanitizer.sanitize("my_file-2023.jpg"));
        assertEquals("audio 01.mp3", sanitizer.sanitize("audio 01.mp3"));
    }

    @Test
    void shouldReplaceDisallowedCharacters() {
        assertEquals("test_file.png", sanitizer.sanitize("test@file.png"));
        assertEquals("file_name_.txt", sanitizer.sanitize("file#name$.txt"));
        assertEquals("jpg", sanitizer.sanitize("*?<>.jpg")); // Leading special chars are trimmed
    }

    @Test
    void shouldRemovePathComponents() {
        assertEquals("file.txt", sanitizer.sanitize("/path/to/file.txt"));
        assertEquals("file.txt", sanitizer.sanitize("..\\..\\file.txt"));
        assertEquals("file.txt", sanitizer.sanitize("../../file.txt"));
    }

    @Test
    void shouldRemovePathTraversalAttempts() {
        assertEquals("file.txt", sanitizer.sanitize("../file.txt"));
        assertEquals("file.txt", sanitizer.sanitize("..\\file.txt"));
        assertEquals("evil.txt", sanitizer.sanitize("....//....//evil.txt"));
    }

    @Test
    void shouldTrimLeadingTrailingSpecialChars() {
        assertEquals("file.txt", sanitizer.sanitize("...file.txt"));
        assertEquals("file.txt", sanitizer.sanitize("___file.txt"));
        assertEquals("file.txt", sanitizer.sanitize("file.txt..."));
        assertEquals("file.txt", sanitizer.sanitize("  file.txt  "));
    }

    @Test
    void shouldLimitLength() {
        String longName = "a".repeat(300) + ".txt";
        String sanitized = sanitizer.sanitize(longName);
        assertTrue(sanitized.length() <= 255);
        assertTrue(sanitized.endsWith(".txt")); // Extension preserved
    }

    @Test
    void shouldPreserveExtensionWhenTruncating() {
        String longName = "a".repeat(300) + ".verylongextension";
        String sanitized = sanitizer.sanitize(longName);
        assertTrue(sanitized.length() <= 255);
        assertTrue(sanitized.endsWith(".verylongextension"));
    }

    @Test
    void shouldRejectNullOrBlank() {
        assertThrows(IllegalArgumentException.class, () -> sanitizer.sanitize(null));
        assertThrows(IllegalArgumentException.class, () -> sanitizer.sanitize(""));
        assertThrows(IllegalArgumentException.class, () -> sanitizer.sanitize("   "));
    }

    @Test
    void shouldRejectFilenamesThatBecomeEmpty() {
        assertThrows(IllegalArgumentException.class, () -> sanitizer.sanitize("..."));
        assertThrows(IllegalArgumentException.class, () -> sanitizer.sanitize("___"));
        assertThrows(IllegalArgumentException.class, () -> sanitizer.sanitize("@#$%"));
    }

    @Test
    void shouldIdentifyUnsafeFilenames() {
        assertFalse(sanitizer.isSafe(null));
        assertFalse(sanitizer.isSafe(""));
        assertFalse(sanitizer.isSafe("../etc/passwd"));
        assertFalse(sanitizer.isSafe("..\\windows\\system32"));
        assertFalse(sanitizer.isSafe("file/with/slash.txt"));
        assertFalse(sanitizer.isSafe("file\\with\\backslash.txt"));
        assertFalse(sanitizer.isSafe("file\u0000null.txt")); // Null byte
    }

    @Test
    void shouldIdentifySafeFilenames() {
        assertTrue(sanitizer.isSafe("test.png"));
        assertTrue(sanitizer.isSafe("my_file-2023.jpg"));
        assertTrue(sanitizer.isSafe("audio 01.mp3"));
        assertTrue(sanitizer.isSafe("document.pdf"));
    }

    @Test
    void shouldHandleFilenamesWithoutExtension() {
        assertEquals("README", sanitizer.sanitize("README"));
        assertEquals("txt", sanitizer.sanitize("...txt")); // Leading dots are trimmed
    }

    @Test
    void shouldHandleMultipleDots() {
        assertEquals("archive.tar.gz", sanitizer.sanitize("archive.tar.gz"));
        assertEquals("file.backup.2023.txt", sanitizer.sanitize("file.backup.2023.txt"));
    }
}
