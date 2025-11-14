package org.memospace.persistence;

import lombok.RequiredArgsConstructor;
import org.memospace.persistence.jpa.entity.DeckEntity;
import org.memospace.persistence.jpa.mapper.DeckJpaMapper;
import org.memospace.persistence.jpa.repository.DeckJpaRepository;
import org.memospace.model.Deck;
import org.memospace.port.DeckRepositoryPort;
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