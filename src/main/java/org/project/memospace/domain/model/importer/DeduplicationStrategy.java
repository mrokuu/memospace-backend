package org.project.memospace.domain.model.importer;

import java.util.Locale;

public enum DeduplicationStrategy {
    NONE("none"),
    FRONT_EQUALS("front-equals"),
    NOTE_HASH("note-hash");

    private final String value;

    DeduplicationStrategy(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static DeduplicationStrategy from(String raw) {
        if (raw == null) {
            return FRONT_EQUALS;
        }
        String normalized = raw.trim().toLowerCase(Locale.ROOT);
        for (DeduplicationStrategy strategy : values()) {
            if (strategy.value.equals(normalized)) {
                return strategy;
            }
        }
        throw new IllegalArgumentException("Unsupported deduplication strategy: " + raw);
    }
}
