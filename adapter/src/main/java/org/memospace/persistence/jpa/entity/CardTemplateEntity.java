package org.memospace.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "note_type_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardTemplateEntity {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_type_id", nullable = false)
    private NoteTypeEntity noteType;

    @Column(nullable = false)
    private String name;

    @Column(name = "front_template", columnDefinition = "TEXT", nullable = false)
    private String frontTemplate;

    @Column(name = "back_template", columnDefinition = "TEXT", nullable = false)
    private String backTemplate;

    @Column(name = "is_cloze", nullable = false)
    private Boolean isCloze;
}