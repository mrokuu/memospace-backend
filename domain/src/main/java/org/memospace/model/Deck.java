package org.memospace.model;

import lombok.Builder;
import lombok.With;

import java.time.LocalDateTime;
import java.util.Objects;

@Builder(toBuilder = true)
public record Deck(@With Long id, String name, String description, LocalDateTime createdAt) {
    public Deck(Long id, String name, String description, LocalDateTime createdAt) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.description = description;
        this.createdAt = Objects.requireNonNull(createdAt, "CreatedAt cannot be null");
    }

    public static Deck create(String name, String description) {
        return new Deck(null, name, description, LocalDateTime.now());
    }

    public Deck updateDetails(String name, String description) {
        return toBuilder()
                .name(name)
                .description(description)
                .build();
    }
}