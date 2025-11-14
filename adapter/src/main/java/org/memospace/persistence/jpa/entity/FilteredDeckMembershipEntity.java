package org.memospace.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "filtered_deck_memberships", indexes = {
        @Index(name = "idx_fdm_filtered_deck_position", columnList = "filtered_deck_id,position"),
        @Index(name = "idx_fdm_card_id", columnList = "card_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(FilteredDeckMembershipEntity.FilteredDeckMembershipId.class)
public class FilteredDeckMembershipEntity {
    @Id
    @Column(name = "filtered_deck_id", columnDefinition = "BINARY(16)")
    private UUID filteredDeckId;

    @Id
    @Column(name = "card_id")
    private Long cardId;

    @Column(nullable = false)
    private Integer position;

    @Column(name = "added_at", nullable = false)
    private Instant addedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FilteredDeckMembershipId implements Serializable {
        private UUID filteredDeckId;
        private Long cardId;
    }
}
