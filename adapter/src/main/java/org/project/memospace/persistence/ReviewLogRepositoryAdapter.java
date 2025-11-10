package org.project.memospace.adapter.persistence;

import lombok.RequiredArgsConstructor;
import org.project.memospace.adapter.persistence.jpa.entity.ReviewLogEntity;
import org.project.memospace.adapter.persistence.jpa.mapper.ReviewLogJpaMapper;
import org.project.memospace.adapter.persistence.jpa.repository.ReviewLogJpaRepository;
import org.project.memospace.domain.model.ReviewLog;
import org.project.memospace.domain.port.ReviewLogRepositoryPort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewLogRepositoryAdapter implements ReviewLogRepositoryPort {
    private final ReviewLogJpaRepository jpaRepository;
    private final ReviewLogJpaMapper mapper;

    @Override
    public ReviewLog save(ReviewLog reviewLog) {
        ReviewLogEntity entity = mapper.toEntity(reviewLog);
        ReviewLogEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public List<ReviewLog> findByDeckIdAndDateRange(Long deckId, LocalDateTime from, LocalDateTime to) {
        return jpaRepository.findByDeckIdAndDateRange(deckId, from, to)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ReviewLog> findByDateRange(LocalDateTime from, LocalDateTime to) {
        return jpaRepository.findByDateRange(from, to)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}