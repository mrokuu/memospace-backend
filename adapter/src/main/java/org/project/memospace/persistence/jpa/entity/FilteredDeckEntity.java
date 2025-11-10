package org.project.memospace.adapter.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.memospace.domain.model.FilteredMode;
import org.project.memospace.domain.model.ReturnPolicy;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "filtered_decks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilteredDeckEntity {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "owner_deck_id")
    private UUID ownerDeckId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String query;

    @Column(name = "card_limit", nullable = false)
    private Integer limit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FilteredMode mode;

    @Enumerated(EnumType.STRING)
    @Column(name = "return_policy", nullable = false)
    private ReturnPolicy returnPolicy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "last_built_at")
    private Instant lastBuiltAt;
}
