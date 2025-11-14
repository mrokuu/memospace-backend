package org.memospace.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cards", indexes = {
        @Index(name = "idx_cards_deck_id", columnList = "deck_id"),
        @Index(name = "idx_cards_due_at", columnList = "due_at"),
        @Index(name = "idx_cards_note_id", columnList = "note_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "deck_id", nullable = false)
    private Long deckId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String front;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String back;

    @Column(columnDefinition = "TEXT")
    private String tags;

    @Column(name = "ease_factor", nullable = false)
    private Double easeFactor;

    @Column(name = "interval_days", nullable = false)
    private Integer intervalDays;

    @Column(nullable = false)
    private Integer repetitions;

    @Column(name = "due_at", nullable = false)
    private LocalDateTime dueAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "note_id", columnDefinition = "BINARY(16)")
    private UUID noteId;

    @Column(name = "template_id", columnDefinition = "BINARY(16)")
    private UUID templateId;

    @Column(name = "cloze_index")
    private Integer clozeIndex;
}