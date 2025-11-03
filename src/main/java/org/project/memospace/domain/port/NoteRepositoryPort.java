package org.project.memospace.domain.port;

import org.project.memospace.domain.model.Note;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NoteRepositoryPort {
    Note save(Note note);

    Optional<Note> findById(UUID id);

    List<Note> findAll(Long deckId, String tag, String searchQuery, int page, int size);

    void deleteById(UUID id);

    List<Note> findByNoteTypeId(UUID noteTypeId);

    boolean existsById(UUID id);

    List<Note> findByDeckId(Long deckId);
}
