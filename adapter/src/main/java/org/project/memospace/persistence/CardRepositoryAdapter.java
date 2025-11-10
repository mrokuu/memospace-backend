package org.project.memospace.adapter.persistence;

import lombok.RequiredArgsConstructor;
import org.project.memospace.adapter.persistence.jpa.entity.CardEntity;
import org.project.memospace.adapter.persistence.jpa.mapper.CardJpaMapper;
import org.project.memospace.adapter.persistence.jpa.repository.CardJpaRepository;
import org.project.memospace.domain.model.Card;
import org.project.memospace.domain.port.CardRepositoryPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CardRepositoryAdapter implements CardRepositoryPort {
    private final CardJpaRepository jpaRepository;
    private final CardJpaMapper mapper;

    @Override
    public Card save(Card card) {
        CardEntity entity = mapper.toEntity(card);
        CardEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Card> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Card> findDueCards(Long deckId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return jpaRepository.findDueCardsByDeckId(deckId, LocalDateTime.now(), pageable)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Card> findDueCards(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return jpaRepository.findDueCards(LocalDateTime.now(), pageable)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Card> searchCards(Long deckId, String searchTerm, Boolean onlyDue, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return jpaRepository.searchCards(deckId, searchTerm, onlyDue != null ? onlyDue : false, LocalDateTime.now(), pageable)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void deleteByDeckId(Long deckId) {
        jpaRepository.deleteByDeckId(deckId);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public List<Card> saveAll(List<Card> cards) {
        List<CardEntity> entities = cards.stream()
                .map(mapper::toEntity)
                .collect(Collectors.toList());
        List<CardEntity> savedEntities = jpaRepository.saveAll(entities);
        return savedEntities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllByNoteId(UUID noteId) {
        jpaRepository.deleteAllByNoteId(noteId);
    }

    @Override
    public List<Card> findAllByNoteId(UUID noteId) {
        return jpaRepository.findAllByNoteId(noteId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}