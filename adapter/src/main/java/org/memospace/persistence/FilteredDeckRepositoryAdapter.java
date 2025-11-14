package org.memospace.persistence;

import lombok.RequiredArgsConstructor;
import org.memospace.persistence.jpa.entity.FilteredDeckEntity;
import org.memospace.persistence.jpa.mapper.FilteredDeckJpaMapper;
import org.memospace.persistence.jpa.repository.FilteredDeckJpaRepository;
import org.memospace.model.FilteredDeck;
import org.memospace.port.FilteredDeckRepositoryPort;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FilteredDeckRepositoryAdapter implements FilteredDeckRepositoryPort {
    private final FilteredDeckJpaRepository jpaRepository;
    private final FilteredDeckJpaMapper mapper;

    @Override
    public FilteredDeck save(FilteredDeck filteredDeck) {
        FilteredDeckEntity entity = mapper.toEntity(filteredDeck);
        FilteredDeckEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<FilteredDeck> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public List<FilteredDeck> findExpired(Instant now) {
        return jpaRepository.findExpired(now).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
