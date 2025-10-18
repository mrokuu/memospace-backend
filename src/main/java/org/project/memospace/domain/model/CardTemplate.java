package org.project.memospace.domain.model;

import lombok.Builder;

import java.util.Objects;
import java.util.UUID;

@Builder(toBuilder = true)
public record CardTemplate(UUID id, String name, String frontTemplate, String backTemplate, boolean isCloze) {
    public CardTemplate(UUID id, String name, String frontTemplate, String backTemplate, boolean isCloze) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.frontTemplate = Objects.requireNonNull(frontTemplate, "Front template cannot be null");
        this.backTemplate = Objects.requireNonNull(backTemplate, "Back template cannot be null");
        this.isCloze = isCloze;
    }

    public static CardTemplate create(String name, String frontTemplate, String backTemplate, boolean isCloze) {
        return new CardTemplate(UUID.randomUUID(), name, frontTemplate, backTemplate, isCloze);
    }
}