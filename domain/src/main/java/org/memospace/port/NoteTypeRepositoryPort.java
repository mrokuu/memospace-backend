package org.memospace.port;

import org.memospace.model.NoteType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NoteTypeRepositoryPort {
    NoteType save(NoteType noteType);

    Optional<NoteType> findById(UUID id);

    List<NoteType> findAll();

    void deleteById(UUID id);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);
}