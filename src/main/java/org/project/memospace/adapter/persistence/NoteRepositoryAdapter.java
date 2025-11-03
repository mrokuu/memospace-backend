package org.project.memospace.adapter.persistence;

import org.project.memospace.adapter.persistence.jpa.mapper.NoteJpaMapper;
import org.project.memospace.adapter.persistence.jpa.repository.NoteJpaRepository;
import org.project.memospace.domain.model.Note;
import org.project.memospace.domain.port.NoteRepositoryPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class NoteRepositoryAdapter implements NoteRepositoryPort {

    private final NoteJpaRepository jpaRepository;
    private final NoteJpaMapper mapper;

    public NoteRepositoryAdapter(NoteJpaRepository jpaRepository, NoteJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Note save(Note note) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(note)));
    }

    @Override
    public Optional<Note> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Note> findAll(Long deckId, String tag, String searchQuery, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return jpaRepository.findNotesWithFilter(deckId, tag, searchQuery, pageable)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Note> findByNoteTypeId(UUID noteTypeId) {
        return jpaRepository.findByNoteTypeId(noteTypeId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public List<Note> findByDeckId(Long deckId) {
        return jpaRepository.findByDeckId(deckId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
