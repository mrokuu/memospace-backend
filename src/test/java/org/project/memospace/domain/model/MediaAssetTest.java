package org.project.memospace.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class MediaAssetTest {

    private static final MediaId MEDIA_ID = MediaId.of("a".repeat(64));
    private static final Instant NOW = Instant.now();

    @Test
    void shouldCreateValidMediaAsset() {
        MediaAsset asset = new MediaAsset(
                MEDIA_ID,
                "test.png",
                "image/png",
                "png",
                1024L,
                NOW,
                0
        );

        assertEquals(MEDIA_ID, asset.getId());
        assertEquals("test.png", asset.getOriginalFilename());
        assertEquals("image/png", asset.getMimeType());
        assertEquals("png", asset.getExtension());
        assertEquals(1024L, asset.getSizeBytes());
        assertEquals(NOW, asset.getCreatedAt());
        assertEquals(0, asset.getUsageCount());
    }

    @Test
    void shouldRejectNullId() {
        assertThrows(IllegalArgumentException.class, () -> new MediaAsset(
                null, "test.png", "image/png", "png", 1024L, NOW, 0
        ));
    }

    @Test
    void shouldRejectNullOrBlankFilename() {
        assertThrows(IllegalArgumentException.class, () -> new MediaAsset(
                MEDIA_ID, null, "image/png", "png", 1024L, NOW, 0
        ));
        assertThrows(IllegalArgumentException.class, () -> new MediaAsset(
                MEDIA_ID, "", "image/png", "png", 1024L, NOW, 0
        ));
        assertThrows(IllegalArgumentException.class, () -> new MediaAsset(
                MEDIA_ID, "   ", "image/png", "png", 1024L, NOW, 0
        ));
    }

    @Test
    void shouldRejectNullOrBlankMimeType() {
        assertThrows(IllegalArgumentException.class, () -> new MediaAsset(
                MEDIA_ID, "test.png", null, "png", 1024L, NOW, 0
        ));
        assertThrows(IllegalArgumentException.class, () -> new MediaAsset(
                MEDIA_ID, "test.png", "", "png", 1024L, NOW, 0
        ));
    }

    @Test
    void shouldRejectNullOrBlankExtension() {
        assertThrows(IllegalArgumentException.class, () -> new MediaAsset(
                MEDIA_ID, "test.png", "image/png", null, 1024L, NOW, 0
        ));
        assertThrows(IllegalArgumentException.class, () -> new MediaAsset(
                MEDIA_ID, "test.png", "image/png", "", 1024L, NOW, 0
        ));
    }

    @Test
    void shouldRejectNegativeSize() {
        assertThrows(IllegalArgumentException.class, () -> new MediaAsset(
                MEDIA_ID, "test.png", "image/png", "png", -1L, NOW, 0
        ));
    }

    @Test
    void shouldRejectNullCreatedAt() {
        assertThrows(IllegalArgumentException.class, () -> new MediaAsset(
                MEDIA_ID, "test.png", "image/png", "png", 1024L, null, 0
        ));
    }

    @Test
    void shouldRejectNegativeUsageCount() {
        assertThrows(IllegalArgumentException.class, () -> new MediaAsset(
                MEDIA_ID, "test.png", "image/png", "png", 1024L, NOW, -1
        ));
    }

    @Test
    void shouldUpdateUsageCount() {
        MediaAsset asset = new MediaAsset(
                MEDIA_ID, "test.png", "image/png", "png", 1024L, NOW, 0
        );

        MediaAsset updated = asset.withUsageCount(5);
        assertEquals(5, updated.getUsageCount());
        assertEquals(0, asset.getUsageCount()); // Original unchanged
    }

    @Test
    void shouldIncrementUsage() {
        MediaAsset asset = new MediaAsset(
                MEDIA_ID, "test.png", "image/png", "png", 1024L, NOW, 5
        );

        MediaAsset incremented = asset.incrementUsage(3);
        assertEquals(8, incremented.getUsageCount());
        assertEquals(5, asset.getUsageCount()); // Original unchanged

        MediaAsset decremented = asset.incrementUsage(-2);
        assertEquals(3, decremented.getUsageCount());
    }

    @Test
    void shouldHandleEquality() {
        MediaAsset asset1 = new MediaAsset(
                MEDIA_ID, "test.png", "image/png", "png", 1024L, NOW, 0
        );
        MediaAsset asset2 = new MediaAsset(
                MEDIA_ID, "other.png", "image/jpeg", "jpg", 2048L, NOW, 5
        );
        MediaAsset asset3 = new MediaAsset(
                MediaId.of("b".repeat(64)), "test.png", "image/png", "png", 1024L, NOW, 0
        );

        assertEquals(asset1, asset2); // Same ID
        assertNotEquals(asset1, asset3); // Different ID
    }
}
