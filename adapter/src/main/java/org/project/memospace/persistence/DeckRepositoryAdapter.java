package org.project.memospace.adapter.persistence;

import lombok.RequiredArgsConstructor;
import org.project.memospace.adapter.persistence.jpa.entity.DeckEntity;
import org.project.memospace.adapter.persistence.jpa.mapper.DeckJpaMapper;
import org.project.memospace.adapter.persistence.jpa.repository.DeckJpaRepository;
import org.project.memospace.domain.model.Deck;
import org.project.memospace.domain.port.DeckRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DeckRepositoryAdapter implements DeckRepositoryPort {
    private final DeckJpaRepository jpaRepository;
    private final DeckJpaMapper mapper;

    @Override
    public Deck save(Deck deck) {
        DeckEntity entity = mapper.toEntity(deck);
        DeckEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Deck> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Deck> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}