package org.memospace.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "note_fields")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@lombok.EqualsAndHashCode(exclude = {"note"})
@lombok.ToString(exclude = {"note"})
public class NoteFieldEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", nullable = false)
    private NoteEntity note;

    @Column(nullable = false)
    private String name;

    @Column(name = "field_value", columnDefinition = "TEXT")
    private String value;
}