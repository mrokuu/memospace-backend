package org.project.memospace.util;

import org.project.memospace.domain.model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Central test fixture factory for creating deterministic test data.
 * All methods use fixed values to ensure test reproducibility.
 */
public class TestFixtures {

    // Fixed timestamps for deterministic tests
    public static final LocalDateTime FIXED_NOW = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
    public static final LocalDateTime FIXED_PAST = FIXED_NOW.minusDays(7);
    public static final LocalDateTime FIXED_FUTURE = FIXED_NOW.plusDays(7);

    // Fixed UUIDs for deterministic tests
    public static final UUID FIXED_UUID_1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
    public static final UUID FIXED_UUID_2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
    public static final UUID FIXED_UUID_3 = UUID.fromString("00000000-0000-0000-0000-000000000003");

    // Fixed IDs
    public static final Long DECK_ID = 100L;
    public static final Long CARD_ID = 200L;
    public static final Long NOTE_ID = 300L;

    // Card Fixtures

    public static Card newCard() {
        return Card.create(DECK_ID, "What is 2+2?", "4", List.of("math"));
    }

    public static Card newCardWithId(Long id) {
        return new Card(
                id,
                DECK_ID,
                "What is 2+2?",
                "4",
                List.of("math"),
                2.5,
                1,
                0,
                FIXED_NOW,
                FIXED_PAST,
                FIXED_NOW,
                null,
                null,
                null
        );
    }

    public static Card experiencedCard() {
        return new Card(
                CARD_ID,
                DECK_ID,
                "Advanced question",
                "Advanced answer",
                List.of("advanced"),
                2.8,
                15,
                5,
                FIXED_FUTURE,
                FIXED_PAST,
                FIXED_NOW,
                FIXED_UUID_1,
                FIXED_UUID_2,
                null
        );
    }

    public static Card clozeCard(Integer clozeIndex) {
        return new Card(
                CARD_ID,
                DECK_ID,
                "{{c1::Paris}} is the capital",
                "Paris",
                List.of("geography"),
                2.5,
                1,
                0,
                FIXED_NOW,
                FIXED_PAST,
                FIXED_NOW,
                FIXED_UUID_1,
                FIXED_UUID_2,
                clozeIndex
        );
    }

    // Deck Fixtures

    public static Deck defaultDeck() {
        return new Deck(
                DECK_ID,
                "Default Deck",
                "A test deck",
                FIXED_PAST
        );
    }

    public static Deck deckWithName(String name) {
        return new Deck(
                DECK_ID,
                name,
                "Description for " + name,
                FIXED_PAST
        );
    }

    // Note Fixtures

    public static Note basicNote() {
        return Note.builder()
                .id(FIXED_UUID_1)
                .noteTypeId(FIXED_UUID_2)
                .deckId(DECK_ID)
                .fieldValue("Front", "What is Java?")
                .fieldValue("Back", "A programming language")
                .tag("programming")
                .createdAt(FIXED_PAST)
                .updatedAt(FIXED_NOW)
                .build();
    }

    public static Note noteWithFields(Map<String, String> fields) {
        return Note.builder()
                .id(FIXED_UUID_1)
                .noteTypeId(FIXED_UUID_2)
                .deckId(DECK_ID)
                .fieldValues(fields)
                .createdAt(FIXED_PAST)
                .updatedAt(FIXED_NOW)
                .build();
    }

    // NoteType Fixtures

    public static NoteType basicNoteType() {
        List<CardTemplate> templates = List.of(
                new CardTemplate(FIXED_UUID_3, "Card 1", "{{Front}}", "{{Back}}", false)
        );

        return NoteType.builder()
                .id(FIXED_UUID_2)
                .name("Basic")
                .fields(List.of("Front", "Back"))
                .templates(templates)
                .css("/* Default CSS */")
                .createdAt(FIXED_PAST)
                .updatedAt(FIXED_NOW)
                .build();
    }

    public static NoteType clozeNoteType() {
        List<CardTemplate> templates = List.of(
                new CardTemplate(FIXED_UUID_3, "Cloze", "{{cloze:Text}}", "{{cloze:Text}}", true)
        );

        return NoteType.builder()
                .id(FIXED_UUID_2)
                .name("Cloze")
                .fields(List.of("Text", "Extra"))
                .templates(templates)
                .css("/* Cloze CSS */")
                .createdAt(FIXED_PAST)
                .updatedAt(FIXED_NOW)
                .build();
    }

    // ReviewResult Fixtures

    public static ReviewResult perfectReview() {
        return ReviewResult.create(4);
    }

    public static ReviewResult goodReview() {
        return ReviewResult.create(3);
    }

    public static ReviewResult failedReview() {
        return ReviewResult.create(2);
    }

    // ReviewLog Fixtures

    public static ReviewLog reviewLog(Long cardId, int quality) {
        return ReviewLog.builder()
                .id(1L)
                .cardId(cardId)
                .quality(quality)
                .reviewedAt(FIXED_NOW)
                .intervalAfter(1)
                .easeFactorAfter(2.5)
                .build();
    }

    // CardTemplate Fixtures

    public static CardTemplate basicTemplate() {
        return new CardTemplate(
                FIXED_UUID_3,
                "Basic Template",
                "{{Front}}",
                "{{Back}}",
                false
        );
    }

    public static CardTemplate templateWithContent(String front, String back) {
        return new CardTemplate(
                FIXED_UUID_3,
                "Custom Template",
                front,
                back,
                false
        );
    }

    // FilteredDeck Fixtures

    public static FilteredDeck filteredDeck() {
        return FilteredDeck.builder()
                .id(FIXED_UUID_1)
                .name("Filtered Deck")
                .ownerDeckId(FIXED_UUID_2)
                .query("tag:important")
                .limit(50)
                .mode(FilteredMode.REVIEW_ONLY)
                .returnPolicy(ReturnPolicy.WHEN_EMPTY)
                .createdAt(java.time.Instant.now())
                .build();
    }

    // MediaAsset Fixtures

    public static MediaAsset imageAsset() {
        return MediaAsset.builder()
                .id(MediaId.of("a" + "0".repeat(63)))
                .originalFilename("test-image.png")
                .mimeType("image/png")
                .extension("png")
                .sizeBytes(1024L)
                .createdAt(java.time.Instant.now())
                .usageCount(0)
                .build();
    }

    public static MediaAsset audioAsset() {
        return MediaAsset.builder()
                .id(MediaId.of("b" + "0".repeat(63)))
                .originalFilename("test-audio.mp3")
                .mimeType("audio/mpeg")
                .extension("mp3")
                .sizeBytes(2048L)
                .createdAt(java.time.Instant.now())
                .usageCount(0)
                .build();
    }

    // Field helper methods

    public static Map<String, String> basicFields() {
        Map<String, String> fields = new java.util.HashMap<>();
        fields.put("Front", "Question");
        fields.put("Back", "Answer");
        return fields;
    }

    public static Map<String, String> fields(String... keyValues) {
        if (keyValues.length % 2 != 0) {
            throw new IllegalArgumentException("Must provide pairs of key-value arguments");
        }
        Map<String, String> fields = new java.util.HashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            fields.put(keyValues[i], keyValues[i + 1]);
        }
        return fields;
    }

    // Builder methods for mutable test data

    public static CardBuilder cardBuilder() {
        return new CardBuilder();
    }

    public static class CardBuilder {
        private Long id = CARD_ID;
        private Long deckId = DECK_ID;
        private String front = "Default Front";
        private String back = "Default Back";
        private List<String> tags = new ArrayList<>(List.of("test"));
        private double easeFactor = 2.5;
        private int intervalDays = 1;
        private int repetitions = 0;
        private LocalDateTime dueAt = FIXED_NOW;
        private UUID noteId = null;
        private UUID templateId = null;
        private Integer clozeIndex = null;

        public CardBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CardBuilder deckId(Long deckId) {
            this.deckId = deckId;
            return this;
        }

        public CardBuilder front(String front) {
            this.front = front;
            return this;
        }

        public CardBuilder back(String back) {
            this.back = back;
            return this;
        }

        public CardBuilder tags(List<String> tags) {
            this.tags = new ArrayList<>(tags);
            return this;
        }

        public CardBuilder easeFactor(double easeFactor) {
            this.easeFactor = easeFactor;
            return this;
        }

        public CardBuilder intervalDays(int intervalDays) {
            this.intervalDays = intervalDays;
            return this;
        }

        public CardBuilder repetitions(int repetitions) {
            this.repetitions = repetitions;
            return this;
        }

        public CardBuilder dueAt(LocalDateTime dueAt) {
            this.dueAt = dueAt;
            return this;
        }

        public CardBuilder noteId(UUID noteId) {
            this.noteId = noteId;
            return this;
        }

        public CardBuilder templateId(UUID templateId) {
            this.templateId = templateId;
            return this;
        }

        public CardBuilder clozeIndex(Integer clozeIndex) {
            this.clozeIndex = clozeIndex;
            return this;
        }

        public Card build() {
            return new Card(
                    id, deckId, front, back, tags,
                    easeFactor, intervalDays, repetitions, dueAt,
                    FIXED_PAST, FIXED_NOW, noteId, templateId, clozeIndex
            );
        }
    }
}
