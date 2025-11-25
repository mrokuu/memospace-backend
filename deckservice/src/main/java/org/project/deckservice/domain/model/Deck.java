package org.project.deckservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class Deck {

    Long id;
    String name;
    String description;
    LocalDateTime createdAt;

    public static Deck create(String name, String description) {
        return new Deck(null, name, description, LocalDateTime.now());
    }

    public Deck updateDetails(String name, String description) {
        return new Deck(this.id, name, description, this.createdAt);
    }
}
