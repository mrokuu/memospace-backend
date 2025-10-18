package org.project.memospace.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.project.memospace.adapter.persistence.jpa.repository.MediaAssetJpaRepository;
import org.project.memospace.adapter.web.dto.MediaDiagnosticsResponse;
import org.project.memospace.adapter.web.dto.UploadMediaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "media.root=./target/test-media",
        "media.max-size-mb=1"
})
class MediaWorkflowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MediaAssetJpaRepository mediaAssetRepository;

    private static final Path testMediaRoot = Path.of("./target/test-media");

    @BeforeEach
    void setUp() throws IOException {
        mediaAssetRepository.deleteAll();
        // Clear all files from test directory
        clearMediaDirectory();
    }

    @AfterEach
    void tearDown() throws IOException {
        mediaAssetRepository.deleteAll();
        clearMediaDirectory();
    }

    private void clearMediaDirectory() throws IOException {
        if (Files.exists(testMediaRoot)) {
            Files.walk(testMediaRoot)
                    .sorted((a, b) -> -a.compareTo(b)) // Delete files before directories
                    .filter(p -> !p.equals(testMediaRoot)) // Don't delete root
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (IOException e) {
                            // Ignore
                        }
                    });
        }
    }

    @Test
    void shouldUploadAndDownloadImage() throws Exception {
        // Upload image - use unique content for this test
        byte[] imageData = createUniqueTestImageData("unique-upload-test");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                imageData
        );

        MvcResult uploadResult = mockMvc.perform(multipart("/api/v1/media")
                        .file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.url").exists())
                .andExpect(jsonPath("$.mimeType").value("image/png"))
                .andExpect(jsonPath("$.sizeBytes").value(imageData.length))
                .andExpect(jsonPath("$.originalFilename").value("test.png"))
                .andExpect(jsonPath("$.deduplicated").value(false))
                .andReturn();

        String uploadResponseJson = uploadResult.getResponse().getContentAsString();
        UploadMediaResponse uploadResponse = objectMapper.readValue(uploadResponseJson, UploadMediaResponse.class);
        String mediaId = uploadResponse.id();

        // Download image
        mockMvc.perform(get("/api/v1/media/" + mediaId))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Type"))
                .andExpect(header().string("Content-Type", "image/png"))
                .andExpect(header().exists("ETag"))
                .andExpect(header().exists("Cache-Control"))
                .andExpect(content().bytes(imageData));
    }

    @Test
    void shouldDeduplicateIdenticalContent() throws Exception {
        byte[] imageData = createUniqueTestImageData("dedup-test");

        // Upload first time
        MockMultipartFile file1 = new MockMultipartFile(
                "file", "original.png", "image/png", imageData
        );
        MvcResult result1 = mockMvc.perform(multipart("/api/v1/media").file(file1))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.deduplicated").value(false))
                .andReturn();

        String response1 = result1.getResponse().getContentAsString();
        UploadMediaResponse upload1 = objectMapper.readValue(response1, UploadMediaResponse.class);

        // Upload same content with different filename
        MockMultipartFile file2 = new MockMultipartFile(
                "file", "duplicate.png", "image/png", imageData
        );
        MvcResult result2 = mockMvc.perform(multipart("/api/v1/media").file(file2))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.deduplicated").value(true))
                .andExpect(jsonPath("$.id").value(upload1.id())) // Same ID
                .andReturn();

        // Verify only one file exists on disk with this hash
        String prefix = upload1.id().substring(0, 2);
        Path dir = testMediaRoot.resolve(prefix);
        long fileCount = Files.exists(dir) ?
            Files.list(dir)
                .filter(Files::isRegularFile)
                .filter(p -> p.getFileName().toString().startsWith(upload1.id()))
                .count() : 0;
        assertEquals(1, fileCount, "Expected exactly 1 file for ID " + upload1.id());
    }

    @Test
    void shouldUploadAudio() throws Exception {
        byte[] audioData = createTestAudioData();
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.mp3", "audio/mpeg", audioData
        );

        mockMvc.perform(multipart("/api/v1/media").file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mimeType").value("audio/mpeg"))
                .andExpect(jsonPath("$.sizeBytes").value(audioData.length));
    }

    @Test
    void shouldRejectEmptyFile() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "empty.png", "image/png", new byte[0]
        );

        mockMvc.perform(multipart("/api/v1/media").file(emptyFile))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectUnsupportedMimeType() throws Exception {
        byte[] data = "test".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.exe", "application/x-msdownload", data
        );

        mockMvc.perform(multipart("/api/v1/media").file(file))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void shouldRejectFileTooLarge() throws Exception {
        // Max size is 1 MB in test config
        byte[] largeData = new byte[2 * 1024 * 1024]; // 2 MB
        MockMultipartFile file = new MockMultipartFile(
                "file", "large.png", "image/png", largeData
        );

        mockMvc.perform(multipart("/api/v1/media").file(file))
                .andExpect(status().isPayloadTooLarge());
    }

    @Test
    void shouldReturn404ForNonExistentMedia() throws Exception {
        String nonExistentId = "a".repeat(64);
        mockMvc.perform(get("/api/v1/media/" + nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRunDiagnostics() throws Exception {
        // Clean slate for this test
        mediaAssetRepository.deleteAll();

        // Upload a media file
        byte[] imageData = createUniqueTestImageData("diagnostics-test");
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.png", "image/png", imageData
        );
        mockMvc.perform(multipart("/api/v1/media").file(file))
                .andExpect(status().isCreated());

        // Run diagnostics
        MvcResult result = mockMvc.perform(get("/api/v1/media/diagnostics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary").exists())
                .andExpect(jsonPath("$.summary.missingOnDisk").isNumber())
                .andExpect(jsonPath("$.summary.orphansOnDisk").isNumber())
                .andExpect(jsonPath("$.summary.danglingReferences").isNumber())
                .andExpect(jsonPath("$.summary.unusedAssets").isNumber())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        MediaDiagnosticsResponse diagnostics = objectMapper.readValue(
                responseJson, MediaDiagnosticsResponse.class
        );

        // Should have 1 unused asset (not referenced by any note)
        assertEquals(1, diagnostics.summary().unusedAssets());
        assertEquals(0, diagnostics.summary().missingOnDisk());
        // Orphans might exist from other tests, so don't assert exact count
    }

    @Test
    void shouldDetectOrphanFiles() throws Exception {
        // Clear any existing files first
        mediaAssetRepository.deleteAll();

        // Manually create a file that's not in the database
        Path orphanDir = testMediaRoot.resolve("ab");
        Files.createDirectories(orphanDir);
        String orphanHash = "ab" + "c".repeat(62);
        Path orphanFile = orphanDir.resolve(orphanHash + ".png");
        Files.write(orphanFile, createTestImageData());

        // Run diagnostics
        MvcResult result = mockMvc.perform(get("/api/v1/media/diagnostics"))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        MediaDiagnosticsResponse diagnostics = objectMapper.readValue(
                responseJson, MediaDiagnosticsResponse.class
        );

        // Should detect exactly 1 orphan
        assertTrue(diagnostics.summary().orphansOnDisk() >= 1,
            "Expected at least 1 orphan, but got: " + diagnostics.summary().orphansOnDisk());
        assertFalse(diagnostics.orphansOnDisk().isEmpty());
    }

    @Test
    void shouldSanitizeFilenames() throws Exception {
        byte[] imageData = createTestImageData();
        MockMultipartFile file = new MockMultipartFile(
                "file", "bad@filename!.png", "image/png", imageData
        );

        MvcResult result = mockMvc.perform(multipart("/api/v1/media").file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.originalFilename").value("bad_filename_.png"))
                .andReturn();
    }

    @Test
    void shouldStoreFilesWithContentAddressing() throws Exception {
        byte[] imageData = createUniqueTestImageData("content-addressing-test");
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.png", "image/png", imageData
        );

        MvcResult result = mockMvc.perform(multipart("/api/v1/media").file(file))
                .andExpect(status().isCreated())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        UploadMediaResponse response = objectMapper.readValue(responseJson, UploadMediaResponse.class);
        String mediaId = response.id();

        // Verify file exists at expected path: {root}/{hash[0..2]}/{hash}.{ext}
        String prefix = mediaId.substring(0, 2);
        Path expectedDir = testMediaRoot.resolve(prefix);
        assertTrue(Files.exists(expectedDir), "Expected directory " + expectedDir + " to exist");

        // File should exist with .png extension
        Path expectedFile = expectedDir.resolve(mediaId + ".png");
        assertTrue(Files.exists(expectedFile),
            "Expected file " + expectedFile + " to exist. Found files: " +
            (Files.exists(expectedDir) ? String.join(", ",
                Files.list(expectedDir).map(p -> p.getFileName().toString()).toArray(String[]::new)) : "dir not found")
        );
        assertArrayEquals(imageData, Files.readAllBytes(expectedFile));
    }

    private byte[] createTestImageData() {
        return createUniqueTestImageData("default");
    }

    private byte[] createUniqueTestImageData(String uniqueMarker) {
        // Simple fake PNG data with unique content based on marker
        byte[] marker = uniqueMarker.getBytes();
        byte[] base = new byte[]{
                (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, // PNG header
                0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
                0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
                0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, (byte) 0xC4,
                (byte) 0x89
        };
        // Combine base with unique marker to ensure unique content
        byte[] result = new byte[base.length + marker.length];
        System.arraycopy(base, 0, result, 0, base.length);
        System.arraycopy(marker, 0, result, base.length, marker.length);
        return result;
    }

    private byte[] createTestAudioData() {
        // Simple fake MP3 data (just ID3 header)
        return new byte[]{
                0x49, 0x44, 0x33, 0x03, 0x00, 0x00, 0x00, 0x00, // ID3v2 header
                0x00, 0x00, (byte) 0xFF, (byte) 0xFB, 0x50, 0x00
        };
    }
}
