package org.project.deckservice.adapter.persistance.repository;

import lombok.RequiredArgsConstructor;
import org.project.deckservice.adapter.persistance.jpa.DeckJpaRepository;
import org.project.deckservice.adapter.persistance.mapper.DeckJpaMapper;
import org.project.deckservice.domain.entity.DeckEntity;
import org.project.deckservice.domain.model.Deck;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DeckRepositoryAdapter {

    private final DeckJpaRepository jpaRepository;
    private final DeckJpaMapper mapper;

    public Deck save(Deck deck) {
        DeckEntity entity = mapper.toEntity(deck);
        DeckEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    public Optional<Deck> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    public List<Deck> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}
