package org.project.memospace.domain.model;

import lombok.Builder;
import lombok.With;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Builder(toBuilder = true)
public record FilteredDeck(@With UUID id, String name, UUID ownerDeckId, String query, int limit, FilteredMode mode,
                           ReturnPolicy returnPolicy, Instant createdAt, Instant expiresAt, Instant lastBuiltAt) {
    public FilteredDeck(UUID id, String name, UUID ownerDeckId, String query, int limit,
                        FilteredMode mode, ReturnPolicy returnPolicy, Instant createdAt,
                        Instant expiresAt, Instant lastBuiltAt) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.ownerDeckId = ownerDeckId;
        this.query = Objects.requireNonNull(query, "Query cannot be null");
        this.limit = limit;
        this.mode = Objects.requireNonNull(mode, "Mode cannot be null");
        this.returnPolicy = Objects.requireNonNull(returnPolicy, "ReturnPolicy cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "CreatedAt cannot be null");
        this.expiresAt = expiresAt;
        this.lastBuiltAt = lastBuiltAt;
    }

    public static FilteredDeck create(String name, UUID ownerDeckId, String query, int limit,
                                      FilteredMode mode, ReturnPolicy returnPolicy, Instant expiresAt,
                                      Instant now) {
        String deckName = name != null ? name : generateDefaultName(now);
        return new FilteredDeck(UUID.randomUUID(), deckName, ownerDeckId, query, limit,
                mode, returnPolicy, now, expiresAt, null);
    }

    public FilteredDeck markRebuilt(Instant now) {
        return toBuilder()
                .lastBuiltAt(now)
                .build();
    }

    public boolean isExpired(Instant now) {
        return expiresAt != null && now.isAfter(expiresAt);
    }

    private static String generateDefaultName(Instant now) {
        return "Filtered " + now.toString();
    }
}
