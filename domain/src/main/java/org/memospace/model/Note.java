package org.memospace.model;

import lombok.Builder;
import lombok.Singular;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Builder(toBuilder = true)
public record Note(UUID id, Long deckId, UUID noteTypeId, @Singular Map<String, String> fieldValues,
                   @Singular Set<String> tags, LocalDateTime createdAt, LocalDateTime updatedAt) {
    public Note(UUID id, Long deckId, UUID noteTypeId, Map<String, String> fieldValues,
                Set<String> tags, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.deckId = Objects.requireNonNull(deckId, "DeckId cannot be null");
        this.noteTypeId = Objects.requireNonNull(noteTypeId, "NoteTypeId cannot be null");
        this.fieldValues = Collections.unmodifiableMap(new HashMap<>(Objects.requireNonNull(fieldValues, "Field values cannot be null")));
        this.tags = Collections.unmodifiableSet(normalizeTags(Objects.requireNonNull(tags, "Tags cannot be null")));
        this.createdAt = Objects.requireNonNull(createdAt, "CreatedAt cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "UpdatedAt cannot be null");

        validateFieldValues();
        validateTags();
    }

    public static Note create(Long deckId, UUID noteTypeId, Map<String, String> fieldValues, Set<String> tags) {
        LocalDateTime now = LocalDateTime.now();
        return new Note(UUID.randomUUID(), deckId, noteTypeId, fieldValues, tags, now, now);
    }

    public Note update(Long deckId, Map<String, String> fieldValues, Set<String> tags) {
        return toBuilder()
                .deckId(deckId)
                .fieldValues(fieldValues)
                .tags(tags)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private void validateFieldValues() {
        for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();

            if (fieldName == null || fieldName.trim().isEmpty()) {
                throw new IllegalArgumentException("Field name cannot be null or empty");
            }
            if (fieldValue != null && fieldValue.length() > 16000) {
                throw new IllegalArgumentException("Field value cannot exceed 16000 characters: " + fieldName);
            }
        }
    }

    private void validateTags() {
        for (String tag : tags) {
            if (tag == null || tag.trim().isEmpty()) {
                throw new IllegalArgumentException("Tag cannot be null or empty");
            }
            if (tag.length() > 64) {
                throw new IllegalArgumentException("Tag cannot exceed 64 characters: " + tag);
            }
        }
    }

    private Set<String> normalizeTags(Set<String> tags) {
        return tags.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    public String getFieldValue(String fieldName) {
        return fieldValues.getOrDefault(fieldName, "");
    }
}