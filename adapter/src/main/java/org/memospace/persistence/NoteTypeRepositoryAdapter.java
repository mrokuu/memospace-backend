package org.memospace.persistence;

import org.memospace.persistence.jpa.mapper.NoteTypeJpaMapper;
import org.memospace.persistence.jpa.repository.NoteTypeJpaRepository;
import org.memospace.model.NoteType;
import org.memospace.port.NoteTypeRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class NoteTypeRepositoryAdapter implements NoteTypeRepositoryPort {

    private final NoteTypeJpaRepository jpaRepository;
    private final NoteTypeJpaMapper mapper;

    public NoteTypeRepositoryAdapter(NoteTypeJpaRepository jpaRepository, NoteTypeJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public NoteType save(NoteType noteType) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(noteType)));
    }

    @Override
    public Optional<NoteType> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<NoteType> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public boolean existsByNameAndIdNot(String name, UUID id) {
        return jpaRepository.existsByNameIgnoreCaseAndIdNot(name, id);
    }
}