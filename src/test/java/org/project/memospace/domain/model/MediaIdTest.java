package org.project.memospace.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MediaIdTest {

    private static final String VALID_HASH = "a".repeat(64);

    @Test
    void shouldCreateValidMediaId() {
        MediaId id = MediaId.of(VALID_HASH);
        assertEquals(VALID_HASH, id.value());
    }

    @Test
    void shouldNormalizeToLowercase() {
        String upperHash = "A".repeat(64);
        MediaId id = MediaId.of(upperHash);
        assertEquals(VALID_HASH, id.value());
    }

    @Test
    void shouldTrimWhitespace() {
        MediaId id = MediaId.of("  " + VALID_HASH + "  ");
        assertEquals(VALID_HASH, id.value());
    }

    @Test
    void shouldRejectNull() {
        assertThrows(IllegalArgumentException.class, () -> MediaId.of(null));
    }

    @Test
    void shouldRejectBlank() {
        assertThrows(IllegalArgumentException.class, () -> MediaId.of(""));
        assertThrows(IllegalArgumentException.class, () -> MediaId.of("   "));
    }

    @Test
    void shouldRejectWrongLength() {
        assertThrows(IllegalArgumentException.class, () -> MediaId.of("a".repeat(63)));
        assertThrows(IllegalArgumentException.class, () -> MediaId.of("a".repeat(65)));
    }

    @Test
    void shouldRejectNonHexCharacters() {
        String invalidHash = "g".repeat(64);
        assertThrows(IllegalArgumentException.class, () -> MediaId.of(invalidHash));

        String withSpecialChars = "a".repeat(63) + "!";
        assertThrows(IllegalArgumentException.class, () -> MediaId.of(withSpecialChars));
    }

    @Test
    void shouldHandleEqualsAndHashCode() {
        MediaId id1 = MediaId.of(VALID_HASH);
        MediaId id2 = MediaId.of(VALID_HASH);
        MediaId id3 = MediaId.of("b".repeat(64));

        assertEquals(id1, id2);
        assertNotEquals(id1, id3);
        assertEquals(id1.hashCode(), id2.hashCode());
        assertNotEquals(id1.hashCode(), id3.hashCode());
    }

    @Test
    void shouldConvertToString() {
        MediaId id = MediaId.of(VALID_HASH);
        assertEquals(VALID_HASH, id.toString());
    }
}
