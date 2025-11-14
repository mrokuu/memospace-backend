package org.memospace.persistence;

import lombok.RequiredArgsConstructor;
import org.memospace.persistence.jpa.entity.CardEntity;
import org.memospace.persistence.jpa.entity.FilteredDeckMembershipEntity;
import org.memospace.persistence.jpa.mapper.CardJpaMapper;
import org.memospace.persistence.jpa.mapper.FilteredDeckMembershipJpaMapper;
import org.memospace.persistence.jpa.repository.FilteredDeckMembershipJpaRepository;
import org.memospace.model.Card;
import org.memospace.model.FilteredDeckMembership;
import org.memospace.port.FilteredDeckMembershipRepositoryPort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FilteredDeckMembershipRepositoryAdapter implements FilteredDeckMembershipRepositoryPort {
    private final FilteredDeckMembershipJpaRepository jpaRepository;
    private final FilteredDeckMembershipJpaMapper mapper;
    private final CardJpaMapper cardMapper;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void saveAll(List<FilteredDeckMembership> memberships) {
        List<FilteredDeckMembershipEntity> entities = memberships.stream()
                .map(mapper::toEntity)
                .toList();

        // Save in batches to avoid memory issues
        int batchSize = 500;
        for (int i = 0; i < entities.size(); i += batchSize) {
            int end = Math.min(i + batchSize, entities.size());
            jpaRepository.saveAll(entities.subList(i, end));
            entityManager.flush();
            entityManager.clear();
        }
    }

    @Override
    @Transactional
    public void deleteAllFor(UUID filteredDeckId) {
        jpaRepository.deleteAllByFilteredDeckId(filteredDeckId);
    }

    @Override
    @Transactional
    public void removeCard(UUID filteredDeckId, Long cardId) {
        jpaRepository.deleteByFilteredDeckIdAndCardId(filteredDeckId, cardId);
    }

    @Override
    public List<Card> findNextDue(UUID filteredDeckId, Instant now, int limit) {
        LocalDateTime nowLocal = LocalDateTime.ofInstant(now, ZoneId.systemDefault());

        String jpql = "SELECT c FROM CardEntity c " +
                "JOIN FilteredDeckMembershipEntity m ON c.id = m.cardId " +
                "WHERE m.filteredDeckId = :filteredDeckId " +
                "AND c.dueAt <= :now " +
                "ORDER BY m.position ASC, c.dueAt ASC, c.id ASC";

        TypedQuery<CardEntity> query = entityManager.createQuery(jpql, CardEntity.class);
        query.setParameter("filteredDeckId", filteredDeckId);
        query.setParameter("now", nowLocal);
        query.setMaxResults(limit);

        return query.getResultList().stream()
                .map(cardMapper::toDomain)
                .toList();
    }

    @Override
    public int countFor(UUID filteredDeckId) {
        return jpaRepository.countByFilteredDeckId(filteredDeckId);
    }
}
