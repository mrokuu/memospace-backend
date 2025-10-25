package org.project.memospace.adapter.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.project.memospace.domain.exception.InvalidMediaException;
import org.project.memospace.domain.exception.MediaNotFoundException;
import org.project.memospace.domain.model.MediaId;
import org.project.memospace.domain.port.MediaStoragePort;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FilesystemMediaStorageAdapterTest {

    @TempDir
    Path tempDir;

    private MimeTypeValidator mimeTypeValidator;
    private FilesystemMediaStorageAdapter storage;

    @BeforeEach
    void setUp() {
        mimeTypeValidator = mock(MimeTypeValidator.class);
        when(mimeTypeValidator.isAllowed(anyString())).thenReturn(true);
        when(mimeTypeValidator.getExtensionForMimeType("image/png")).thenReturn("png");
        when(mimeTypeValidator.getExtensionForMimeType("image/jpeg")).thenReturn("jpg");

        storage = new FilesystemMediaStorageAdapter(tempDir, mimeTypeValidator, 10 * 1024 * 1024); // 10MB
    }

    @Test
    void shouldSaveNewMediaFile() throws IOException {
        // Given
        byte[] content = "test image content".getBytes();
        InputStream data = new ByteArrayInputStream(content);

        // When
        MediaStoragePort.SaveResult result = storage.save(data, "test.png", "image/png");

        // Then
        assertNotNull(result.getId());
        assertFalse(result.isDeduplicated());
        assertEquals(content.length, result.getSizeBytes());
        assertTrue(storage.exists(result.getId()));
    }

    @Test
    void shouldDeduplicateIdenticalContent() throws IOException {
        // Given
        byte[] content = "identical content".getBytes();

        // When
        MediaStoragePort.SaveResult result1 = storage.save(
                new ByteArrayInputStream(content), "file1.png", "image/png");
        MediaStoragePort.SaveResult result2 = storage.save(
                new ByteArrayInputStream(content), "file2.png", "image/png");

        // Then
        assertEquals(result1.getId(), result2.getId());
        assertFalse(result1.isDeduplicated());
        assertTrue(result2.isDeduplicated());
    }

    @Test
    void shouldGenerateDifferentIdsForDifferentContent() {
        // Given
        byte[] content1 = "content one".getBytes();
        byte[] content2 = "content two".getBytes();

        // When
        MediaStoragePort.SaveResult result1 = storage.save(
                new ByteArrayInputStream(content1), "file1.png", "image/png");
        MediaStoragePort.SaveResult result2 = storage.save(
                new ByteArrayInputStream(content2), "file2.png", "image/png");

        // Then
        assertNotEquals(result1.getId(), result2.getId());
    }

    @Test
    void shouldRetrieveSavedMedia() throws IOException {
        // Given
        byte[] content = "retrievable content".getBytes();
        MediaStoragePort.SaveResult saveResult = storage.save(
                new ByteArrayInputStream(content), "test.png", "image/png");

        // When
        try (InputStream retrieved = storage.get(saveResult.getId())) {
            byte[] retrievedContent = retrieved.readAllBytes();

            // Then
            assertArrayEquals(content, retrievedContent);
        }
    }

    @Test
    void shouldThrowExceptionForNonExistentMedia() {
        // Given
        MediaId nonExistentId = MediaId.of("a".repeat(64));

        // When / Then
        assertThrows(MediaNotFoundException.class, () -> storage.get(nonExistentId));
    }

    @Test
    void shouldReturnCorrectExistenceStatus() {
        // Given
        byte[] content = "existence test".getBytes();
        MediaStoragePort.SaveResult saveResult = storage.save(
                new ByteArrayInputStream(content), "test.png", "image/png");
        MediaId nonExistentId = MediaId.of("b".repeat(64));

        // When / Then
        assertTrue(storage.exists(saveResult.getId()));
        assertFalse(storage.exists(nonExistentId));
    }

    @Test
    void shouldListAllStoredMedia() {
        // Given
        storage.save(new ByteArrayInputStream("content1".getBytes()), "file1.png", "image/png");
        storage.save(new ByteArrayInputStream("content2".getBytes()), "file2.png", "image/png");
        storage.save(new ByteArrayInputStream("content3".getBytes()), "file3.png", "image/png");

        // When
        List<MediaId> allMedia = storage.listAll();

        // Then
        assertEquals(3, allMedia.size());
    }

    @Test
    void shouldReturnCorrectFileSize() {
        // Given
        byte[] content = "size test content".getBytes();
        MediaStoragePort.SaveResult saveResult = storage.save(
                new ByteArrayInputStream(content), "test.png", "image/png");

        // When
        long size = storage.size(saveResult.getId());

        // Then
        assertEquals(content.length, size);
    }

    @Test
    void shouldThrowExceptionForSizeOfNonExistentMedia() {
        // Given
        MediaId nonExistentId = MediaId.of("c".repeat(64));

        // When / Then
        assertThrows(MediaNotFoundException.class, () -> storage.size(nonExistentId));
    }

    @Test
    void shouldRejectUnsupportedMimeType() {
        // Given
        when(mimeTypeValidator.isAllowed("application/exe")).thenReturn(false);
        byte[] content = "malicious content".getBytes();

        // When / Then
        assertThrows(InvalidMediaException.class, () ->
                storage.save(new ByteArrayInputStream(content), "virus.exe", "application/exe")
        );
    }

    @Test
    void shouldRejectEmptyFile() {
        // Given
        byte[] emptyContent = new byte[0];

        // When / Then
        assertThrows(InvalidMediaException.class, () ->
                storage.save(new ByteArrayInputStream(emptyContent), "empty.png", "image/png")
        );
    }

    @Test
    void shouldRejectOversizedFile() {
        // Given
        FilesystemMediaStorageAdapter smallStorage = new FilesystemMediaStorageAdapter(
                tempDir.resolve("small"), mimeTypeValidator, 100); // 100 bytes limit
        byte[] largeContent = new byte[200];

        // When / Then
        assertThrows(InvalidMediaException.class, () ->
                smallStorage.save(new ByteArrayInputStream(largeContent), "large.png", "image/png")
        );
    }

    @Test
    void shouldUseContentAddressedPaths() {
        // Given
        byte[] content = "path test".getBytes();
        MediaStoragePort.SaveResult saveResult = storage.save(
                new ByteArrayInputStream(content), "test.png", "image/png");

        // When
        Path physicalPath = storage.getPhysicalPath(saveResult.getId());

        // Then
        assertNotNull(physicalPath);
        String hash = saveResult.getId().value();
        assertTrue(physicalPath.toString().contains(hash.substring(0, 2))); // Directory prefix
        assertTrue(physicalPath.toString().contains(hash)); // Full hash in filename
    }

    @Test
    void shouldExtractExtensionFromFilename() {
        // Given
        byte[] content = "extension test".getBytes();

        // When
        MediaStoragePort.SaveResult result = storage.save(
                new ByteArrayInputStream(content), "image.jpg", "image/jpeg");

        // Then
        Path path = storage.getPhysicalPath(result.getId());
        assertTrue(path.toString().endsWith(".jpg"));
    }

    @Test
    void shouldFallbackToMimeTypeForExtension() {
        // Given
        byte[] content = "no extension".getBytes();
        when(mimeTypeValidator.getExtensionForMimeType("image/png")).thenReturn("png");

        // When
        MediaStoragePort.SaveResult result = storage.save(
                new ByteArrayInputStream(content), "noextension", "image/png");

        // Then
        Path path = storage.getPhysicalPath(result.getId());
        assertTrue(path.toString().endsWith(".png"));
    }

    @Test
    void shouldHandleMultipleFilesInSameDirectory() {
        // Given - Save files that will have same directory prefix (first 2 chars of hash)
        // We can't easily control the hash, but we can save multiple files and verify they coexist

        // When
        storage.save(new ByteArrayInputStream("file1".getBytes()), "f1.png", "image/png");
        storage.save(new ByteArrayInputStream("file2".getBytes()), "f2.png", "image/png");
        storage.save(new ByteArrayInputStream("file3".getBytes()), "f3.png", "image/png");

        // Then
        List<MediaId> allMedia = storage.listAll();
        assertEquals(3, allMedia.size());
    }
}
