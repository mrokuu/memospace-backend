package org.project.memospace.adapter.persistence.jpa.repository;

import org.project.memospace.adapter.persistence.jpa.entity.NoteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NoteJpaRepository extends JpaRepository<NoteEntity, UUID> {

    List<NoteEntity> findByNoteTypeId(UUID noteTypeId);

    List<NoteEntity> findByDeckId(Long deckId);

    @Query("""
        SELECT DISTINCT n FROM NoteEntity n
        LEFT JOIN n.fields f
        LEFT JOIN n.tags t
        WHERE (:deckId IS NULL OR n.deckId = :deckId)
        AND (:tag IS NULL OR t.tag = :tag)
        AND (:searchQuery IS NULL OR
             LOWER(f.value) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR
             LOWER(t.tag) LIKE LOWER(CONCAT('%', :searchQuery, '%')))
        ORDER BY n.updatedAt DESC
        """)
    Page<NoteEntity> findNotesWithFilter(@Param("deckId") Long deckId,
                                        @Param("tag") String tag,
                                        @Param("searchQuery") String searchQuery,
                                        Pageable pageable);
}
