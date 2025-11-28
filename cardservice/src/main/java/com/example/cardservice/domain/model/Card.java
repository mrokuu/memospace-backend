package com.example.cardservice.domain.model;

import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Value
public class Card {

    Long id;
    Long deckId;
    String front;
    String back;
    List<String> tags;
    double easeFactor;
    int intervalDays;
    int repetitions;
    LocalDateTime dueAt;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    UUID noteId;
    UUID templateId;
    Integer clozeIndex;

    public Card(Long id, Long deckId, String front, String back, List<String> tags,
                double easeFactor, int intervalDays, int repetitions,
                LocalDateTime dueAt, LocalDateTime createdAt, LocalDateTime updatedAt,
                UUID noteId, UUID templateId, Integer clozeIndex) {
        this.id = id;
        this.deckId = Objects.requireNonNull(deckId, "DeckId cannot be null");
        this.front = Objects.requireNonNull(front, "Front cannot be null");
        this.back = Objects.requireNonNull(back, "Back cannot be null");
        this.tags = Objects.requireNonNull(tags, "Tags cannot be null");
        this.easeFactor = easeFactor;
        this.intervalDays = intervalDays;
        this.repetitions = repetitions;
        this.dueAt = Objects.requireNonNull(dueAt, "DueAt cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "CreatedAt cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "UpdatedAt cannot be null");
        this.noteId = noteId;
        this.templateId = templateId;
        this.clozeIndex = clozeIndex;
    }

    public static Card create(Long deckId, String front, String back, List<String> tags) {
        LocalDateTime now = LocalDateTime.now();
        return new Card(null, deckId, front, back, tags, 2.5, 1, 0, now, now, now, null, null, null);
    }

    public Card updateContent(String front, String back, List<String> tags) {
        return new Card(this.id, this.deckId, front, back, tags,
                this.easeFactor, this.intervalDays, this.repetitions,
                this.dueAt, this.createdAt, LocalDateTime.now(), this.noteId, this.templateId, this.clozeIndex);
    }

    public Card updateScheduling(double easeFactor, int intervalDays, int repetitions, LocalDateTime dueAt) {
        return new Card(this.id, this.deckId, this.front, this.back, this.tags,
                easeFactor, intervalDays, repetitions, dueAt, this.createdAt, LocalDateTime.now(),
                this.noteId, this.templateId, this.clozeIndex);
    }
}
